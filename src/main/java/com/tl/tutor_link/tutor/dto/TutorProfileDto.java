package com.tl.tutor_link.tutor.dto;

import com.tl.tutor_link.tutor.model.Faculty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TutorProfileDto {
    private Long id;
    private Long userId;
    private String username;
    private String firstname;
    private String lastname;

    private String bio;
    private String tagline;
    private List<CourseDto> courses;
    private List<Faculty> faculties;
    private String location;
    private boolean remote;
    private Integer hourlyRate;
    private String profileImageKey;
    private String profileImageUrl;
    private Double longitude;
    private Double latitude;
    private List<TutorLanguageDto> languages;
    private List<TutorCredentialDto> credentials;
    private List<TutorStyleDto> styles;
}
