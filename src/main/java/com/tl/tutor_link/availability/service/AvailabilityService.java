package com.tl.tutor_link.availability.service;

import com.tl.tutor_link.availability.dto.AvailabilityRuleDto;
import com.tl.tutor_link.availability.dto.BookableDatesDto;
import com.tl.tutor_link.availability.dto.OpenSlotsDto;
import com.tl.tutor_link.availability.mapper.AvailabilityRuleMapper;
import com.tl.tutor_link.availability.model.AvailabilityRule;
import com.tl.tutor_link.availability.repository.AvailabilityRuleRepository;
import com.tl.tutor_link.booking.model.Booking;
import com.tl.tutor_link.booking.model.BookingStatus;
import com.tl.tutor_link.booking.repository.BookingRepository;
import com.tl.tutor_link.common.config.AppConstants;
import com.tl.tutor_link.common.exception.BadRequestException;
import com.tl.tutor_link.common.exception.ErrorCode;
import com.tl.tutor_link.common.exception.ResourceNotFoundException;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import com.tl.tutor_link.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Weekly recurring availability, and the open-slot computation used both to
 * show bookable times to students and to validate incoming bookings.
 */
@Service
public class AvailabilityService {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");
    private static final List<BookingStatus> BLOCKING =
            List.of(BookingStatus.PENDING, BookingStatus.ACCEPTED);

    private final AvailabilityRuleRepository ruleRepository;
    private final TutorRepository tutorRepository;
    private final BookingRepository bookingRepository;
    private final AvailabilityRuleMapper availabilityRuleMapper;

    public AvailabilityService(
            AvailabilityRuleRepository ruleRepository,
            TutorRepository tutorRepository,
            BookingRepository bookingRepository,
            AvailabilityRuleMapper availabilityRuleMapper
    ) {
        this.ruleRepository = ruleRepository;
        this.tutorRepository = tutorRepository;
        this.bookingRepository = bookingRepository;
        this.availabilityRuleMapper = availabilityRuleMapper;
    }

    // -----------------------------------------------------------------
    // Tutor-facing rule management
    // -----------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<AvailabilityRuleDto> getMyRules(User user) {
        return rulesFor(tutorOf(user)).stream().map(availabilityRuleMapper::toDto).toList();
    }

    @Transactional
    public List<AvailabilityRuleDto> replaceMyRules(User user, List<AvailabilityRuleDto> dtos) {
        Tutor tutor = tutorOf(user);
        validate(dtos);

        ruleRepository.deleteByTutor(tutor);
        List<AvailabilityRule> saved = ruleRepository.saveAll(dtos.stream().map(dto -> {
            AvailabilityRule rule = new AvailabilityRule();
            rule.setTutor(tutor);
            rule.setDayOfWeek(dto.getDayOfWeek());
            rule.setStartTime(dto.getStartTime());
            rule.setEndTime(dto.getEndTime());
            return rule;
        }).toList());

        return saved.stream().map(availabilityRuleMapper::toDto).toList();
    }

    // -----------------------------------------------------------------
    // Open slots
    // -----------------------------------------------------------------

    @Transactional(readOnly = true)
    public OpenSlotsDto getOpenSlots(Long tutorId, LocalDate date, int durationMinutes) {
        Tutor tutor = tutorById(tutorId);
        List<AvailabilityRule> rules = rulesFor(tutor);
        if (rules.isEmpty()) return new OpenSlotsDto(false, List.of());

        List<LocalTime> slots =
                slotsOn(rules, date, durationMinutes, bookingsBetween(tutor, date, date));
        return new OpenSlotsDto(true, slots.stream().map(HH_MM::format).toList());
    }

    /**
     * Which dates in the range have at least one open slot, so the booking
     * calendar can grey out dead days instead of making the student click
     * each one to find nothing there.
     */
    @Transactional(readOnly = true)
    public BookableDatesDto getBookableDates(Long tutorId, LocalDate from, LocalDate to, int duration) {
        if (to.isBefore(from) || ChronoUnit.DAYS.between(from, to) > AppConstants.MAX_AVAILABILITY_RANGE_DAYS) {
            throw new BadRequestException(
                    "Range must run forwards and be at most "
                            + AppConstants.MAX_AVAILABILITY_RANGE_DAYS + " days");
        }

        Tutor tutor = tutorById(tutorId);
        List<AvailabilityRule> rules = rulesFor(tutor);
        if (rules.isEmpty()) return new BookableDatesDto(false, List.of());

        // One booking query for the whole range rather than one per day.
        List<Booking> active = bookingsBetween(tutor, from, to);

        List<String> dates = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            if (!slotsOn(rules, d, duration, active).isEmpty()) dates.add(d.toString());
        }
        return new BookableDatesDto(true, dates);
    }

    /**
     * Rejects a booking that collides with an existing one, or - when the tutor
     * has set hours - that starts outside them.
     */
    @Transactional(readOnly = true)
    public void assertBookable(Tutor tutor, LocalDateTime start, int durationMinutes) {
        LocalDate date = start.toLocalDate();
        List<Booking> active = bookingsBetween(tutor, date, date);
        LocalDateTime end = start.plusMinutes(durationMinutes);

        if (active.stream().anyMatch(b -> overlaps(start, end, b))) {
            throw new BadRequestException(
                    "The tutor is already booked at this time. Please pick another slot.",
                    ErrorCode.SLOT_UNAVAILABLE);
        }

        List<AvailabilityRule> rules = rulesFor(tutor);
        if (!rules.isEmpty()
                && !slotsOn(rules, date, durationMinutes, active).contains(start.toLocalTime())) {
            throw new BadRequestException(
                    "That time is outside the tutor's availability. Please pick an open slot.",
                    ErrorCode.SLOT_UNAVAILABLE);
        }
    }

    // -----------------------------------------------------------------
    // Internals
    // -----------------------------------------------------------------

    /**
     * Start times on the given date that fit the duration inside a window and
     * clash with nothing. Uses minute-of-day arithmetic because LocalTime wraps
     * at midnight, which would let a long session "fit" a late window.
     */
    private List<LocalTime> slotsOn(
            List<AvailabilityRule> rules, LocalDate date, int duration, List<Booking> active
    ) {
        LocalDateTime now = LocalDateTime.now();
        List<LocalTime> slots = new ArrayList<>();

        for (AvailabilityRule rule : rules) {
            if (rule.getDayOfWeek() != date.getDayOfWeek()) continue;

            int end = rule.getEndTime().toSecondOfDay() / 60;
            for (int m = rule.getStartTime().toSecondOfDay() / 60;
                 m + duration <= end;
                 m += AppConstants.SLOT_INCREMENT_MINUTES) {

                LocalTime time = LocalTime.of(m / 60, m % 60);
                LocalDateTime slotStart = date.atTime(time);
                LocalDateTime slotEnd = slotStart.plusMinutes(duration);

                if (slotStart.isAfter(now) && active.stream().noneMatch(b -> overlaps(slotStart, slotEnd, b))) {
                    slots.add(time);
                }
            }
        }
        return slots.stream().distinct().sorted().toList();
    }

    /** Widened at the front so a session starting the previous evening still counts. */
    private List<Booking> bookingsBetween(Tutor tutor, LocalDate from, LocalDate to) {
        return bookingRepository.findByTutorAndStatusInAndScheduledAtBetween(
                tutor, BLOCKING,
                from.atStartOfDay().minusMinutes(AppConstants.MAX_BOOKING_DURATION_MINUTES),
                to.plusDays(1).atStartOfDay());
    }

    private boolean overlaps(LocalDateTime start, LocalDateTime end, Booking booking) {
        LocalDateTime bStart = booking.getScheduledAt();
        return start.isBefore(bStart.plusMinutes(booking.getDurationMinutes())) && end.isAfter(bStart);
    }

    private void validate(List<AvailabilityRuleDto> dtos) {
        for (AvailabilityRuleDto dto : dtos) {
            if (!dto.getStartTime().isBefore(dto.getEndTime())) {
                throw new BadRequestException("Each window must end after it starts");
            }
            if (dto.getStartTime().getMinute() % AppConstants.SLOT_INCREMENT_MINUTES != 0
                    || dto.getEndTime().getMinute() % AppConstants.SLOT_INCREMENT_MINUTES != 0) {
                throw new BadRequestException("Times must be on the half hour");
            }
        }

        List<AvailabilityRuleDto> sorted = dtos.stream()
                .sorted(Comparator.comparing(AvailabilityRuleDto::getDayOfWeek)
                        .thenComparing(AvailabilityRuleDto::getStartTime))
                .toList();
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).getDayOfWeek() == sorted.get(i - 1).getDayOfWeek()
                    && sorted.get(i).getStartTime().isBefore(sorted.get(i - 1).getEndTime())) {
                String day = sorted.get(i).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                throw new BadRequestException("Windows on " + day + " overlap");
            }
        }
    }

    private List<AvailabilityRule> rulesFor(Tutor tutor) {
        return ruleRepository.findByTutorOrderByDayOfWeekAscStartTimeAsc(tutor);
    }

    private Tutor tutorOf(User user) {
        return tutorRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor profile not found"));
    }

    private Tutor tutorById(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", id));
    }
}
