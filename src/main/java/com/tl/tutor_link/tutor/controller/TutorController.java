package com.tl.tutor_link.tutor.controller;

import com.tl.tutor_link.tutor.dto.TutorProfileDto;
import com.tl.tutor_link.tutor.dto.TutorProfileRequestDto;
import com.tl.tutor_link.tutor.dto.TutorSearchRequestDto;
import com.tl.tutor_link.tutor.service.TutorService;
import com.tl.tutor_link.user.model.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles tutor related endpoints such as tutor profile manage, tutor browsing and tutor search.
 */
@RestController
@RequestMapping("/tutors")
public class TutorController {
    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;

    }

    @PutMapping("/me/profile")
    public ResponseEntity<TutorProfileDto> updateTutorProfile(
            @AuthenticationPrincipal User user,
            @RequestBody TutorProfileRequestDto dto) {
        return ResponseEntity.ok(tutorService.updateTutorProfile(user, dto));
    }

    @PostMapping("/me/profile")
    public ResponseEntity<TutorProfileDto> createTutorProfile(
            @AuthenticationPrincipal User user,
            @RequestBody TutorProfileRequestDto dto
    ) {
        return ResponseEntity.ok(tutorService.createTutorProfile(user, dto));
    }

    @GetMapping
    public ResponseEntity<List<TutorProfileDto>> getTutors(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Boolean remote
    ) {
        TutorSearchRequestDto request = new TutorSearchRequestDto();
        request.setSubject(subject);
        request.setLocation(location);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setRemote(remote);

        return ResponseEntity.ok(tutorService.searchTutors(request));
    }

    @GetMapping("/me/profile")
    public ResponseEntity<TutorProfileDto> getMyTutorProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tutorService.getMyTutorProfile(user));
    }


    @GetMapping("/{id}")
    public ResponseEntity<TutorProfileDto> getTutorById(@PathVariable Long id) {
        return ResponseEntity.ok(tutorService.getTutorById(id));
    }
}
