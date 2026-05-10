package com.tl.tutor_link.tutor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorStyleDto {
    private Long id;
    @NotBlank(message = "Style label is required")
    @Size(max = 50, message = "Style label must be at most 50 characters")
    private String label;
    @NotBlank(message = "Style description is required")
    @Size(max = 200, message = "Style description must be at most 200 characters")
    private String description;
}