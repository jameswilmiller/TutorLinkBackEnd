package com.tl.tutor_link.tutor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseDto {
    private Long id;
    private String courseCode;
    private String courseName;
    private String faculty;
}