package com.tl.tutor_link.tutor.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorSearchRequestDto {
    private String subject;
    private Double latitude;
    private Double longitude;
}
