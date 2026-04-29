package com.tl.tutor_link.tutor.dto;

import com.tl.tutor_link.tutor.model.LanguageLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorLanguageRequestDto {
    private String language;
    private LanguageLevel level;
}