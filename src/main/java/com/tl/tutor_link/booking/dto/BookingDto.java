package com.tl.tutor_link.booking.dto;

import com.tl.tutor_link.booking.model.BookingStatus;
import com.tl.tutor_link.booking.model.SessionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Full view of a booking returned to API clients. Contains identifying
 * info for both parties and the course, plus all booking details.
 */
@Getter
@Setter
public class BookingDto {

    private Long id;

    // Tutor identity
    private Long tutorId;
    private String tutorName;
    private String tutorEmail;

    // Student identity
    private Long studentId;
    private String studentName;
    private String studentEmail;

    // Course
    private Long courseId;
    private String courseCode;
    private String courseName;

    // Booking detail
    private LocalDateTime scheduledAt;
    private String meetingLink;
    private Integer durationMinutes;
    private SessionType sessionType;
    private BookingStatus status;
    private String message;
    private LocalDateTime createdAt;
}