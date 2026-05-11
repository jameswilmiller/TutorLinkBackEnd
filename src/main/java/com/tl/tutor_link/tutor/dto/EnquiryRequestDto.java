package com.tl.tutor_link.tutor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Student-initiated booking request sent to a tutor. The student's name and
 * email are pulled from the authenticated user, not this DTO, to prevent
 * identity spoofing.
 */
@Getter
@Setter
public class EnquiryRequestDto {
    @NotBlank(message = "Course is required")
    @Size(max = 100, message = "Course must be at most 100 characters")
    private String course;

    @NotBlank(message = "Session type is required")
    @Pattern(regexp = "^(online|in-person)$", message = "Session type must be 'online' or 'in-person'")
    private String sessionType;

    @Size(max = 1000, message = "Message must be at most 1000 characters")
    private String message;
}