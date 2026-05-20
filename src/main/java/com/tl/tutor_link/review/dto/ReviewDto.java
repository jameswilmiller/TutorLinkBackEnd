package com.tl.tutor_link.review.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Full view of a review returned to API clients.
 */
@Getter
@Setter
public class ReviewDto {

    private Long id;

    // Tutor
    private Long tutorId;

    // Student identity
    private Long studentId;
    private String studentName;

    // Linked booking
    private Long bookingId;
    private String courseCode;

    // Review content
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}