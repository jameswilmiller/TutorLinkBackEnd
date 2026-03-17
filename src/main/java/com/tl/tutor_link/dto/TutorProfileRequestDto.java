package com.tl.tutor_link.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorProfileRequestDto {
    private String bio;
    private String subjects;
    private String location;
    private boolean remote;
    private Integer hourlyRate;

}
