package com.tl.tutor_link.tutor.service;

import com.tl.tutor_link.tutor.dto.CourseDto;
import com.tl.tutor_link.tutor.mapper.CourseMapper;
import com.tl.tutor_link.tutor.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    public List<CourseDto> search(String query) {
        return courseRepository
                .findByCourseCodeContainingIgnoreCaseOrCourseNameContainingIgnoreCase(query, query)
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }

    public List<CourseDto> getByFaculty(String faculty) {
        return courseRepository.findByFaculty(faculty)
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }
}