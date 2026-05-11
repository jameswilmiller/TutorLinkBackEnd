package com.tl.tutor_link.tutor.service;

import com.tl.tutor_link.tutor.dto.CourseDto;
import com.tl.tutor_link.tutor.mapper.CourseMapper;
import com.tl.tutor_link.tutor.model.Faculty;
import com.tl.tutor_link.tutor.repository.CourseRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Read-only access to the course catalogue. Used by the frontend
 * autocomplete and by tutors selecting which courses they teach.
 */
@Service
public class CourseService {
    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Transactional(readOnly = true)
    public List<CourseDto> search(String query) {
        log.debug("Course search: query={}", query);
        return courseRepository
                .findByCourseCodeContainingIgnoreCaseOrCourseNameContainingIgnoreCase(query, query)
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseDto> getByFaculty(String faculty) {
        log.debug("Courses by faculty: {}", faculty);
        return courseRepository.findByFaculty(faculty)
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }
}