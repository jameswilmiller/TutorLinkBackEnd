package com.tl.tutor_link.tutor.dto;

import com.tl.tutor_link.tutor.model.LanguageLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorLanguageDto {
    private Long id;
    @NotBlank(message = "Language is required")
    @Size(max = 50, message = "Language must be at most 50 characters")
    private String language;
    @NotNull(message = "Language level is required")
    private LanguageLevel level;
}