package com.tl.tutor_link.controller;

import com.tl.tutor_link.dto.TutorProfileDto;
import com.tl.tutor_link.dto.TutorProfileRequestDto;
import com.tl.tutor_link.model.User;
import com.tl.tutor_link.service.TutorService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutors")
public class TutorController {
    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;

    }

    @PostMapping("/me/profile")
    public ResponseEntity<TutorProfileDto> createTutorProfile(
            @RequestBody TutorProfileRequestDto dto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                tutorService.createTutorProfile(user, dto)
        );
    }

    @GetMapping("/me/profile")
    public ResponseEntity<TutorProfileDto> getMyTutorProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                tutorService.getMyTutorProfile(user)
        );
    }

    @GetMapping
    public ResponseEntity<List<TutorProfileDto>> getAllTutors() {

        return ResponseEntity.ok(
                tutorService.getTutors()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TutorProfileDto> getTutorById(@PathVariable Long id) {

        return ResponseEntity.ok(
                tutorService.getTutorById(id)
        );
    }
}
