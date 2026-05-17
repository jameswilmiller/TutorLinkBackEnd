package com.tl.tutor_link.booking.service;

import com.tl.tutor_link.booking.dto.BookingDto;
import com.tl.tutor_link.booking.dto.BookingRequestDto;
import com.tl.tutor_link.booking.mapper.BookingMapper;
import com.tl.tutor_link.booking.model.Booking;
import com.tl.tutor_link.booking.model.BookingStatus;
import com.tl.tutor_link.booking.repository.BookingRepository;
import com.tl.tutor_link.common.exception.BadRequestException;
import com.tl.tutor_link.common.exception.ForbiddenException;
import com.tl.tutor_link.common.exception.ResourceNotFoundException;
import com.tl.tutor_link.notification.service.NotificationService;
import com.tl.tutor_link.tutor.model.Course;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.tutor.repository.CourseRepository;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import com.tl.tutor_link.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Booking lifecycle: creation, status transitions (accept, decline, cancel,
 * complete), and listing. Enforces the booking state machine (see
 * {@link BookingStatus}) and the authorization rules around who can perform
 * each transition. Sends an email notification on every state change.
 */
@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final TutorRepository tutorRepository;
    private final CourseRepository courseRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

    public BookingService(
            BookingRepository bookingRepository,
            TutorRepository tutorRepository,
            CourseRepository courseRepository,
            BookingMapper bookingMapper,
            NotificationService notificationService
    ) {
        this.bookingRepository = bookingRepository;
        this.tutorRepository = tutorRepository;
        this.courseRepository = courseRepository;
        this.bookingMapper = bookingMapper;
        this.notificationService = notificationService;
    }

    /**
     * The notifications sent to the student when their booking changes state.
     * Pairs the action verb (used in the email body) with the subject line,
     * so the two can't drift apart.
     */
    private enum StudentNotification {
        ACCEPTED("accepted", "Your booking has been accepted"),
        DECLINED("declined", "Your booking was declined"),
        COMPLETED("completed", "Session completed — leave a review");

        private final String action;
        private final String subject;

        StudentNotification(String action, String subject) {
            this.action = action;
            this.subject = subject;
        }
    }

    // -----------------------------------------------------------------
    // Creation
    // -----------------------------------------------------------------

    @Transactional
    public BookingDto createBooking(BookingRequestDto dto, User student) {
        log.info("User {} creating booking with tutor {}", student.getId(), dto.getTutorId());

        Tutor tutor = findTutorOrThrow(dto.getTutorId());
        Course course = findCourseOrThrow(dto.getCourseId());

        if (tutor.getUser().getId().equals(student.getId())) {
            throw new BadRequestException("You cannot book a session with yourself");
        }

        Booking booking = new Booking();
        booking.setTutor(tutor);
        booking.setStudent(student);
        booking.setCourse(course);
        booking.setScheduledAt(dto.getScheduledAt());
        booking.setDurationMinutes(dto.getDurationMinutes());
        booking.setSessionType(dto.getSessionType());
        booking.setMessage(dto.getMessage());
        booking.setStatus(BookingStatus.PENDING);

        Booking saved = bookingRepository.save(booking);
        log.info("Booking {} created", saved.getId());

        notifyTutorOfNewBooking(saved);
        return bookingMapper.toDto(saved);
    }

    // -----------------------------------------------------------------
    // Status transitions
    // -----------------------------------------------------------------

    @Transactional
    public BookingDto acceptBooking(Long bookingId, User tutorUser) {
        Booking booking = findBookingOrThrow(bookingId);
        requireTutor(booking, tutorUser);
        transitionTo(booking, BookingStatus.ACCEPTED);
        log.info("Booking {} accepted by tutor {}", bookingId, tutorUser.getId());

        notifyStudent(booking, StudentNotification.ACCEPTED);
        return bookingMapper.toDto(booking);
    }

    @Transactional
    public BookingDto declineBooking(Long bookingId, User tutorUser) {
        Booking booking = findBookingOrThrow(bookingId);
        requireTutor(booking, tutorUser);
        transitionTo(booking, BookingStatus.DECLINED);
        log.info("Booking {} declined by tutor {}", bookingId, tutorUser.getId());

        notifyStudent(booking, StudentNotification.DECLINED);
        return bookingMapper.toDto(booking);
    }

    @Transactional
    public BookingDto cancelBooking(Long bookingId, User caller) {
        Booking booking = findBookingOrThrow(bookingId);
        requireParticipant(booking, caller);
        transitionTo(booking, BookingStatus.CANCELLED);
        log.info("Booking {} cancelled by user {}", bookingId, caller.getId());

        notifyOtherPartyOfCancellation(booking, caller);
        return bookingMapper.toDto(booking);
    }

    @Transactional
    public BookingDto completeBooking(Long bookingId, User tutorUser) {
        Booking booking = findBookingOrThrow(bookingId);
        requireTutor(booking, tutorUser);
        transitionTo(booking, BookingStatus.COMPLETED);
        log.info("Booking {} marked complete by tutor {}", bookingId, tutorUser.getId());

        notifyStudent(booking, StudentNotification.COMPLETED);
        return bookingMapper.toDto(booking);
    }

    @Transactional
    public BookingDto updateMeetingLink(Long bookingId, String meetingLink, User tutorUser) {
        Booking booking = findBookingOrThrow(bookingId);
        requireTutor(booking, tutorUser);
        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new BadRequestException("Meeting link can only be set on accepted bookings");
        }

        booking.setMeetingLink(meetingLink);
        log.info("Booking {} meeting link updated by tutor {}", bookingId, tutorUser.getId());

        notifyStudentOfMeetingLink(booking);
        return bookingMapper.toDto(booking);
    }
    // -----------------------------------------------------------------
    // Reads
    // -----------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<BookingDto> getStudentBookings(User student) {
        log.debug("Fetching bookings for student {}", student.getId());
        return bookingRepository.findByStudentOrderByScheduledAtDesc(student)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getTutorBookings(User tutorUser) {
        log.debug("Fetching bookings for tutor user {}", tutorUser.getId());

        Tutor tutor = tutorRepository.findByUser(tutorUser)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor profile not found"));

        return bookingRepository.findByTutorOrderByScheduledAtDesc(tutor)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, User caller) {
        Booking booking = findBookingOrThrow(bookingId);
        requireParticipant(booking, caller);
        return bookingMapper.toDto(booking);
    }

    // -----------------------------------------------------------------
    // Lookups
    // -----------------------------------------------------------------

    private Booking findBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
    }

    private Tutor findTutorOrThrow(Long tutorId) {
        return tutorRepository.findById(tutorId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", tutorId));
    }

    private Course findCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
    }

    // -----------------------------------------------------------------
    // Authorization + state transition
    // -----------------------------------------------------------------

    /**
     * Verifies the caller is the tutor on this booking.
     */
    private void requireTutor(Booking booking, User caller) {
        if (!booking.getTutor().getUser().getId().equals(caller.getId())) {
            throw new ForbiddenException("Only the tutor can perform this action");
        }
    }

    /**
     * Verifies the caller is either the student or the tutor on this booking.
     */
    private void requireParticipant(Booking booking, User caller) {
        boolean isStudent = booking.getStudent().getId().equals(caller.getId());
        boolean isTutor = booking.getTutor().getUser().getId().equals(caller.getId());
        if (!isStudent && !isTutor) {
            throw new ForbiddenException("You are not a participant in this booking");
        }
    }

    /**
     * Moves the booking to the target state if the state machine allows it,
     * otherwise throws. Centralises every status change in the service.
     */
    private void transitionTo(Booking booking, BookingStatus target) {
        if (!booking.getStatus().canTransitionTo(target)) {
            throw new BadRequestException(
                    "Cannot change a " + booking.getStatus() + " booking to " + target
            );
        }
        booking.setStatus(target);
    }

    // -----------------------------------------------------------------
    // Notifications
    // -----------------------------------------------------------------
    private void notifyStudentOfMeetingLink(Booking booking) {
        notificationService.send(
                booking.getStudent().getEmail(),
                "Meeting link added to your booking",
                BookingEmails.meetingLinkAddedBody(booking),
                "meeting link added notification"
        );
    }

    private void notifyTutorOfNewBooking(Booking booking) {
        notificationService.send(
                booking.getTutor().getUser().getEmail(),
                BookingEmails.newBookingSubject(booking),
                BookingEmails.newBookingBody(booking),
                "new booking notification"
        );
    }

    private void notifyStudent(Booking booking, StudentNotification notification) {
        notificationService.send(
                booking.getStudent().getEmail(),
                notification.subject,
                BookingEmails.statusChangeBody(booking, notification.action),
                "booking " + notification.action + " notification"
        );
    }

    private void notifyOtherPartyOfCancellation(Booking booking, User canceller) {
        boolean cancelledByStudent = booking.getStudent().getId().equals(canceller.getId());
        User recipient = cancelledByStudent
                ? booking.getTutor().getUser()
                : booking.getStudent();

        notificationService.send(
                recipient.getEmail(),
                "Booking cancelled",
                BookingEmails.cancellationBody(booking, canceller),
                "booking cancellation notification"
        );
    }
}