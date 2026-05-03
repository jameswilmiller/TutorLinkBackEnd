package com.tl.tutor_link.tutor.mapper;

import com.tl.tutor_link.tutor.dto.CourseDto;
import com.tl.tutor_link.tutor.model.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {
    public CourseDto toDto(Course course) {
        if (course == null) return null;
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setCourseCode(course.getCourseCode());
        dto.setCourseName(course.getCourseName());
        dto.setFaculty(course.getFaculty());
        return dto;
    }
}