package com.tl.tutor_link.tutor.controller;

import com.tl.tutor_link.tutor.dto.CourseDto;
import com.tl.tutor_link.tutor.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseDto>> search(@RequestParam String query) {
        return ResponseEntity.ok(courseService.search(query));
    }

    @GetMapping("/faculty/{faculty}")
    public ResponseEntity<List<CourseDto>> getByFaculty(@PathVariable String faculty) {
        return ResponseEntity.ok(courseService.getByFaculty(faculty));
    }
}