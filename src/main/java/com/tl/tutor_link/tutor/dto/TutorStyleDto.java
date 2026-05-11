package com.tl.tutor_link.tutor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * A teaching style a tutor identifies with (e.g. "Patient", "Hands-on")
 * with a short description.
 */
@Getter
@Setter
public class TutorStyleDto {

    private Long id;
    private String label;
    private String description;
}