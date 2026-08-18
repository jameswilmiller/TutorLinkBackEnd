package com.tl.tutor_link.availability.service;

import com.tl.tutor_link.availability.dto.AvailabilityRuleDto;
import com.tl.tutor_link.availability.dto.OpenSlotsDto;
import com.tl.tutor_link.availability.mapper.AvailabilityRuleMapper;
import com.tl.tutor_link.availability.model.AvailabilityRule;
import com.tl.tutor_link.availability.repository.AvailabilityRuleRepository;
import com.tl.tutor_link.booking.model.Booking;
import com.tl.tutor_link.booking.repository.BookingRepository;
import com.tl.tutor_link.common.exception.BadRequestException;
import com.tl.tutor_link.support.TestDataFactory;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import com.tl.tutor_link.user.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private AvailabilityRuleRepository ruleRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AvailabilityRuleMapper availabilityRuleMapper;

    @InjectMocks
    private AvailabilityService availabilityService;

    // Next Monday, always in the future
    private static final LocalDate MONDAY = nextMonday();

    private static LocalDate nextMonday() {
        LocalDate d = LocalDate.now().plusDays(1);
        while (d.getDayOfWeek() != DayOfWeek.MONDAY) d = d.plusDays(1);
        return d;
    }

    private Tutor tutorWithWindow(LocalTime start, LocalTime end) {
        Tutor tutor = TestDataFactory.tutor(TestDataFactory.tutorUser());
        tutor.setId(1L);

        AvailabilityRule rule = new AvailabilityRule();
        rule.setTutor(tutor);
        rule.setDayOfWeek(DayOfWeek.MONDAY);
        rule.setStartTime(start);
        rule.setEndTime(end);

        lenient().when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        lenient().when(ruleRepository.findByTutorOrderByDayOfWeekAscStartTimeAsc(tutor))
                .thenReturn(List.of(rule));
        return tutor;
    }

    private Booking bookingAt(Tutor tutor, LocalDateTime start, int minutes) {
        Booking booking = new Booking();
        booking.setTutor(tutor);
        booking.setScheduledAt(start);
        booking.setDurationMinutes(minutes);
        return booking;
    }

    // -----------------------------------------------------------------
    // getOpenSlots
    // -----------------------------------------------------------------

    @Test
    void getOpenSlots_generatesHalfHourStartsThatFitTheWindow() {
        Tutor tutor = tutorWithWindow(LocalTime.of(9, 0), LocalTime.of(11, 0));
        when(bookingRepository.findByTutorAndStatusInAndScheduledAtBetween(
                eq(tutor), any(), any(), any())).thenReturn(List.of());

        OpenSlotsDto result = availabilityService.getOpenSlots(1L, MONDAY, 60);

        // 60-minute session in a 9:00-11:00 window: last viable start is 10:00
        assertThat(result.isHasAvailability()).isTrue();
        assertThat(result.getSlots()).containsExactly("09:00", "09:30", "10:00");
    }

    @Test
    void getOpenSlots_excludesSlotsOverlappingActiveBookings() {
        Tutor tutor = tutorWithWindow(LocalTime.of(9, 0), LocalTime.of(12, 0));
        when(bookingRepository.findByTutorAndStatusInAndScheduledAtBetween(
                eq(tutor), any(), any(), any()))
                .thenReturn(List.of(bookingAt(tutor, MONDAY.atTime(10, 0), 60)));

        OpenSlotsDto result = availabilityService.getOpenSlots(1L, MONDAY, 60);

        // 9:30 would run into the 10:00 booking; 10:00 and 10:30 collide with it
        assertThat(result.getSlots()).containsExactly("09:00", "11:00");
    }

    @Test
    void getOpenSlots_whenTutorHasNoRules_reportsNoAvailability() {
        Tutor tutor = TestDataFactory.tutor(TestDataFactory.tutorUser());
        tutor.setId(1L);
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(ruleRepository.findByTutorOrderByDayOfWeekAscStartTimeAsc(tutor)).thenReturn(List.of());

        OpenSlotsDto result = availabilityService.getOpenSlots(1L, MONDAY, 60);

        assertThat(result.isHasAvailability()).isFalse();
        assertThat(result.getSlots()).isEmpty();
    }

    @Test
    void getOpenSlots_onADayWithNoWindow_returnsNoSlots() {
        Tutor tutor = tutorWithWindow(LocalTime.of(9, 0), LocalTime.of(11, 0));
        OpenSlotsDto result = availabilityService.getOpenSlots(1L, MONDAY.plusDays(1), 60);

        assertThat(result.isHasAvailability()).isTrue();
        assertThat(result.getSlots()).isEmpty();
    }

    @Test
    void getOpenSlots_longDurationNearMidnight_doesNotWrapPastWindowEnd() {
        Tutor tutor = tutorWithWindow(LocalTime.of(21, 0), LocalTime.of(23, 30));
        when(bookingRepository.findByTutorAndStatusInAndScheduledAtBetween(
                eq(tutor), any(), any(), any())).thenReturn(List.of());

        OpenSlotsDto result = availabilityService.getOpenSlots(1L, MONDAY, 240);

        // A 4-hour session cannot fit anywhere in a 2.5-hour evening window
        assertThat(result.getSlots()).isEmpty();
    }

    // -----------------------------------------------------------------
    // assertBookable
    // -----------------------------------------------------------------

    @Test
    void assertBookable_whenSlotIsOpen_passes() {
        Tutor tutor = tutorWithWindow(LocalTime.of(9, 0), LocalTime.of(12, 0));
        when(bookingRepository.findByTutorAndStatusInAndScheduledAtBetween(
                eq(tutor), any(), any(), any())).thenReturn(List.of());

        assertThatCode(() ->
                availabilityService.assertBookable(tutor, MONDAY.atTime(9, 30), 60)
        ).doesNotThrowAnyException();
    }

    @Test
    void assertBookable_whenTimeOutsideWindows_throws() {
        Tutor tutor = tutorWithWindow(LocalTime.of(9, 0), LocalTime.of(12, 0));
        when(bookingRepository.findByTutorAndStatusInAndScheduledAtBetween(
                eq(tutor), any(), any(), any())).thenReturn(List.of());

        assertThatThrownBy(() ->
                availabilityService.assertBookable(tutor, MONDAY.atTime(14, 0), 60)
        ).isInstanceOf(BadRequestException.class)
                .hasMessageContaining("outside the tutor's availability");
    }

    @Test
    void assertBookable_whenOverlappingExistingBooking_throwsEvenWithoutRules() {
        // The overlap check fires before availability is even consulted,
        // so no existsByTutor stub is needed here.
        Tutor tutor = TestDataFactory.tutor(TestDataFactory.tutorUser());
        tutor.setId(1L);
        when(bookingRepository.findByTutorAndStatusInAndScheduledAtBetween(
                eq(tutor), any(), any(), any()))
                .thenReturn(List.of(bookingAt(tutor, MONDAY.atTime(10, 0), 60)));

        assertThatThrownBy(() ->
                availabilityService.assertBookable(tutor, MONDAY.atTime(10, 30), 60)
        ).isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already booked");
    }

    @Test
    void assertBookable_whenNoRulesAndNoConflict_passesAnyTime() {
        Tutor tutor = TestDataFactory.tutor(TestDataFactory.tutorUser());
        tutor.setId(1L);
        when(ruleRepository.findByTutorOrderByDayOfWeekAscStartTimeAsc(tutor)).thenReturn(List.of());
        when(bookingRepository.findByTutorAndStatusInAndScheduledAtBetween(
                eq(tutor), any(), any(), any())).thenReturn(List.of());

        assertThatCode(() ->
                availabilityService.assertBookable(tutor, MONDAY.atTime(6, 15), 60)
        ).doesNotThrowAnyException();
    }

    // -----------------------------------------------------------------
    // replaceMyRules validation
    // -----------------------------------------------------------------

    @Test
    void replaceMyRules_rejectsOverlappingWindowsOnTheSameDay() {
        User user = TestDataFactory.tutorUser();
        Tutor tutor = TestDataFactory.tutor(user);
        when(tutorRepository.findByUser(user)).thenReturn(Optional.of(tutor));

        AvailabilityRuleDto first = rule(DayOfWeek.MONDAY, "09:00", "12:00");
        AvailabilityRuleDto second = rule(DayOfWeek.MONDAY, "11:00", "14:00");

        assertThatThrownBy(() ->
                availabilityService.replaceMyRules(user, List.of(first, second))
        ).isInstanceOf(BadRequestException.class)
                .hasMessageContaining("overlap");
    }

    @Test
    void replaceMyRules_rejectsEndBeforeStart() {
        User user = TestDataFactory.tutorUser();
        Tutor tutor = TestDataFactory.tutor(user);
        when(tutorRepository.findByUser(user)).thenReturn(Optional.of(tutor));

        assertThatThrownBy(() ->
                availabilityService.replaceMyRules(user, List.of(rule(DayOfWeek.MONDAY, "12:00", "09:00")))
        ).isInstanceOf(BadRequestException.class);
    }

    private AvailabilityRuleDto rule(DayOfWeek day, String start, String end) {
        AvailabilityRuleDto dto = new AvailabilityRuleDto();
        dto.setDayOfWeek(day);
        dto.setStartTime(LocalTime.parse(start));
        dto.setEndTime(LocalTime.parse(end));
        return dto;
    }
}
