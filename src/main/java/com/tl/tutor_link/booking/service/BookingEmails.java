package com.tl.tutor_link.booking.service;

import com.tl.tutor_link.booking.model.Booking;
import com.tl.tutor_link.user.model.User;
import org.springframework.web.util.HtmlUtils;

import java.time.format.DateTimeFormatter;

/**
 * Builds the HTML bodies and subject lines for booking-related emails.
 * Pure string construction — no sending, no side effects.
 *
 * All user-provided values (names, messages) are HTML-escaped before being
 * interpolated into the email body, since the body is sent as HTML.
 */
final class BookingEmails {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEEE d MMMM yyyy 'at' h:mm a");

    private BookingEmails() {

    }

    static String newBookingSubject(Booking booking) {
        return "New booking request from " + fullName(booking.getStudent());
    }

    static String newBookingBody(Booking booking) {
        return "<h2>New booking request</h2>"
                + "<p><strong>From:</strong> " + escape(fullName(booking.getStudent()))
                + " (" + escape(booking.getStudent().getEmail()) + ")</p>"
                + "<p><strong>Course:</strong> " + escape(booking.getCourse().getCourseCode())
                + " — " + escape(booking.getCourse().getCourseName()) + "</p>"
                + "<p><strong>When:</strong> " + formatSchedule(booking) + "</p>"
                + "<p><strong>Session type:</strong> " + booking.getSessionType() + "</p>"
                + messageBlock(booking)
                + "<hr><p>Log in to TutorLink to accept or decline this request.</p>";
    }

    static String statusChangeBody(Booking booking, String action) {
        return "<h2>Booking " + action + "</h2>"
                + "<p>Your booking with " + escape(fullName(booking.getTutor().getUser()))
                + " has been <strong>" + action + "</strong>.</p>"
                + "<p><strong>Course:</strong> " + escape(booking.getCourse().getCourseCode()) + "</p>"
                + "<p><strong>When:</strong> " + formatSchedule(booking) + "</p>"
                + "<hr><p>Log in to TutorLink to view your bookings.</p>";
    }

    static String cancellationBody(Booking booking, User canceller) {
        return "<h2>Booking cancelled</h2>"
                + "<p>" + escape(fullName(canceller)) + " has cancelled the following booking:</p>"
                + "<p><strong>Course:</strong> " + escape(booking.getCourse().getCourseCode()) + "</p>"
                + "<p><strong>When:</strong> " + formatSchedule(booking) + "</p>"
                + "<hr><p>Log in to TutorLink to view your bookings.</p>";
    }

    static String meetingLinkAddedBody(Booking booking) {
        return "<h2>Meeting link added</h2>"
                + "<p>" + escape(fullName(booking.getTutor().getUser()))
                + " has added a meeting link for your upcoming session.</p>"
                + "<p><strong>Course:</strong> " + escape(booking.getCourse().getCourseCode()) + "</p>"
                + "<p><strong>When:</strong> " + formatSchedule(booking) + "</p>"
                + "<p><strong>Meeting link:</strong> "
                + "<a href=\"" + escape(booking.getMeetingLink()) + "\">"
                + escape(booking.getMeetingLink()) + "</a></p>"
                + "<hr><p>Log in to TutorLink to view your bookings.</p>";
    }

    private static String fullName(User user) {
        return user.getFirstname() + " " + user.getLastname();
    }

    private static String formatSchedule(Booking booking) {
        return booking.getScheduledAt().format(DATE_FORMAT)
                + " (" + booking.getDurationMinutes() + " minutes)";
    }

    private static String messageBlock(Booking booking) {
        if (booking.getMessage() == null || booking.getMessage().isBlank()) {
            return "";
        }
        return "<p><strong>Message:</strong></p><p>" + escape(booking.getMessage()) + "</p>";
    }

    private static String escape(String value) {
        return value == null ? "" : HtmlUtils.htmlEscape(value);
    }
}