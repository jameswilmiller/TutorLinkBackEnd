package com.tl.tutor_link.tutor.controller;

import com.tl.tutor_link.common.config.AppConstants;
import com.tl.tutor_link.tutor.dto.EnquiryRequestDto;
import com.tl.tutor_link.tutor.dto.TutorProfileDto;
import com.tl.tutor_link.tutor.dto.TutorProfileRequestDto;
import com.tl.tutor_link.tutor.dto.TutorSearchRequestDto;
import com.tl.tutor_link.tutor.model.Faculty;
import com.tl.tutor_link.tutor.service.TutorService;
import com.tl.tutor_link.user.model.User;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handles tutor related endpoints such as tutor profile management, tutor browsing and tutor search.
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
            @Valid @RequestBody TutorProfileRequestDto dto) {
        return ResponseEntity.ok(tutorService.updateTutorProfile(user, dto));
    }

    @PostMapping("/me/profile")
    public ResponseEntity<TutorProfileDto> createTutorProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TutorProfileRequestDto dto
    ) {
        return ResponseEntity.ok(tutorService.createTutorProfile(user, dto));
    }

    @GetMapping
    public ResponseEntity<Page<TutorProfileDto>> getTutors(
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) Faculty faculty,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Boolean remote,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_SIZE) int size
    ) {
        TutorSearchRequestDto request = new TutorSearchRequestDto();
        request.setCourseCode(courseCode);
        request.setFaculty(faculty);
        request.setLocation(location);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setRemote(remote);
        request.setSort(sort);

        Pageable pageable = PageRequest.of(page, size, getSortOrder(sort));
        return ResponseEntity.ok(tutorService.searchTutors(request, pageable));
    }

    @GetMapping("/me/profile")
    public ResponseEntity<TutorProfileDto> getMyTutorProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tutorService.getMyTutorProfile(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TutorProfileDto> getTutorById(@PathVariable Long id) {
        return ResponseEntity.ok(tutorService.getTutorById(id));
    }

    @PostMapping("/{id}/enquire")
    public ResponseEntity<Map<String, String>> enquire(
            @PathVariable Long id,
            @Valid @RequestBody EnquiryRequestDto dto,
            @AuthenticationPrincipal User user
    ) {
        tutorService.handleEnquiry(id, dto, user);
        return ResponseEntity.ok(Map.of("status", "sent"));
    }

    private Sort getSortOrder(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "id");
        }
        return switch (sort) {
            case "price_low" -> Sort.by(Sort.Direction.ASC, "hourlyRate");
            case "price_high" -> Sort.by(Sort.Direction.DESC, "hourlyRate");
            default -> Sort.by(Sort.Direction.DESC, "id");
        };
    }
}
