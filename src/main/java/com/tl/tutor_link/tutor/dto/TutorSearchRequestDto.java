package com.tl.tutor_link.tutor.dto;
import com.tl.tutor_link.tutor.model.Faculty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorSearchRequestDto {
    private String courseCode;
    private Faculty faculty;
    private String subject;
    private String location;
    private Double latitude;
    private Double longitude;
    private Boolean remote;
    private String sort;
}
