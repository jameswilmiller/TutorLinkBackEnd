package com.tl.tutor_link.tutor.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * A UQ course as returned to clients. Used in course autocomplete results
 * and embedded in tutor profiles.
 */
@Getter
@Setter
public class CourseDto {
    private Long id;
    private String courseCode;
    private String courseName;
    private String faculty;
}