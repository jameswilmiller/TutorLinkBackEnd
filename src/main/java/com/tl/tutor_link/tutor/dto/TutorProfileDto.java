package com.tl.tutor_link.tutor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorProfileDto {
    private Long id;
    private Long userId;
    private String username;
    private String firstname;
    private String lastname;

    private String bio;
    private String subjects;
    private String location;
    private boolean remote;
    private Integer hourlyRate;
    private String profileImageKey;
    private String profileImageUrl;
    private Double longitude;
    private Double latitude;
}
