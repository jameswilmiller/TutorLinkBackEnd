package com.tl.tutor_link.tutor.dto;

import com.tl.tutor_link.tutor.model.Faculty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TutorProfileRequestDto {
    private String bio;
    private String tagline;
    private List<Long> courseIds;
    private List<Faculty> faculties;
    private String location;
    private boolean remote;
    private Integer hourlyRate;
    private String profileImageKey;
    private Double longitude;
    private Double latitude;
    private List<TutorLanguageRequestDto> languages;
    private List<TutorStyleRequestDto>  styles;
    private List<TutorCredentialRequestDto> credentials;
}
