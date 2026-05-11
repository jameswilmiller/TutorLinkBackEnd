package com.tl.tutor_link.tutor.dto;

import com.tl.tutor_link.tutor.model.Faculty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The full public view of a tutor profile. Includes the user identity,
 * a presigned image URL (regenerated per request), and nested
 * collections of courses, languages, credentials, and teaching styles.
 */
@Getter
@Setter
public class TutorProfileDto {

    // Identity
    private Long id;
    private Long userId;
    private String username;
    private String firstname;
    private String lastname;

    // Profile content
    private String bio;
    private String tagline;
    private String profileImageKey;
    private String profileImageUrl;

    // Location
    private String location;
    private Double latitude;
    private Double longitude;
    private boolean remote;

    // Pricing
    private Integer hourlyRate;

    // Academic scope
    private List<CourseDto> courses;
    private List<Faculty> faculties;

    // Profile detail
    private List<TutorLanguageDto> languages;
    private List<TutorCredentialDto> credentials;
    private List<TutorStyleDto> styles;
}
