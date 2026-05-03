package com.tl.tutor_link.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.tutor_link.tutor.model.Course;
import com.tl.tutor_link.tutor.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@Order(1)
public class CourseSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    public CourseSeeder(CourseRepository courseRepository, ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (courseRepository.count() > 0) return;

        ClassPathResource resource = new ClassPathResource("courses.json");
        InputStream inputStream = resource.getInputStream();

        List<Map<String, String>> rawCourses = objectMapper.readValue(
                inputStream,
                new TypeReference<>() {}
        );

        List<Course> courses = rawCourses.stream().map(raw -> {
            Course course = new Course();
            course.setCourseCode(raw.get("courseCode"));
            course.setCourseName(raw.get("courseName"));
            course.setFaculty(raw.get("faculty"));
            return course;
        }).toList();

        courseRepository.saveAll(courses);
    }
}