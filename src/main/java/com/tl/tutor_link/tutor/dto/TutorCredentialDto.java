package com.tl.tutor_link.tutor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorCredentialDto {
    private Long id;
    @NotBlank(message = "Credential title is required")
    @Size(max = 200, message = "Credential title must be at most 200 characters")
    private String title;
    @Size(max = 200, message = "Institution must be at most 200 characters")
    private String institution;
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
}