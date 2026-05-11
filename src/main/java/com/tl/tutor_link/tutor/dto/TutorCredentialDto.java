package com.tl.tutor_link.tutor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * A credential (degree, certification, or qualification) displayed on a
 * tutor's profile.
 */
@Getter
@Setter
public class TutorCredentialDto {

    private Long id;
    private String title;
    private String institution;
    private Integer year;
}