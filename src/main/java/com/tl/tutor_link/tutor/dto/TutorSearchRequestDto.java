package com.tl.tutor_link.tutor.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorSearchRequestDto {
    private String subject;
    private String location;
    private Double latitude;
    private Double longitude;
    private Boolean remote;
}
