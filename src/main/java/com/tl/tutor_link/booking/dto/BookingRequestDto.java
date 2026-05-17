package com.tl.tutor_link.booking.dto;

import com.tl.tutor_link.booking.model.SessionType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Submitted by a student to request a booking with a tutor.
 */
@Getter
@Setter
public class BookingRequestDto {

    @NotNull(message = "Tutor is required")
    private Long tutorId;

    @NotNull(message = "Course is required")
    private Long courseId;

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Duration is required")
    @Min(value = 30, message = "Duration must be at least 30 minutes")
    @Max(value = 240, message = "Duration must be at most 240 minutes")
    private Integer durationMinutes;

    @NotNull(message = "Session type is required")
    private SessionType sessionType;

    @Size(max = 1000, message = "Message must be at most 1000 characters")
    private String message;
}