package com.tl.tutor_link.tutor.repository;

import com.tl.tutor_link.tutor.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCourseCodeContainingIgnoreCaseOrCourseNameContainingIgnoreCase(
            String courseCode, String courseName
    );
    List<Course> findByFaculty(String faculty);
    Optional<Course> findByCourseCode(String courseCode);
}