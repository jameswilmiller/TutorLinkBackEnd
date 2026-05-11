package com.tl.tutor_link.tutor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Submitted when a tutor adds or updates a teaching style on their profile.
 */
@Getter
@Setter
public class TutorStyleRequestDto {

    @NotBlank(message = "Style label is required")
    @Size(max = 50, message = "Style label must be at most 50 characters")
    private String label;

    @NotBlank(message = "Style description is required")
    @Size(max = 200, message = "Style description must be at most 200 characters")
    private String description;
}
