package com.tl.tutor_link.tutor.dto;

import com.tl.tutor_link.tutor.model.LanguageLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * A language a tutor speaks, with their proficiency level.
 */
@Getter
@Setter
public class TutorLanguageDto {

    private Long id;
    private String language;
    private LanguageLevel level;
}