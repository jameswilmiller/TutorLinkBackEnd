package com.tl.tutor_link.tutor;

import com.tl.tutor_link.dto.TutorProfileDto;
import com.tl.tutor_link.dto.TutorProfileRequestDto;
import com.tl.tutor_link.dto.TutorSearchRequestDto;
import com.tl.tutor_link.user.model.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

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

    @PutMapping("/me/profile")
    public ResponseEntity<TutorProfileDto> updateTutorProfile(
            @RequestBody TutorProfileRequestDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(tutorService.updateTutorProfile(user, dto));
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

    @PostMapping("/search")
    public ResponseEntity<List<TutorProfileDto>> searchTutors(
        @RequestBody TutorSearchRequestDto request) {
        return ResponseEntity.ok(tutorService.searchTutors(request));
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
