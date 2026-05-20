package com.tl.tutor_link.review.controller;

import com.tl.tutor_link.review.dto.ReviewDto;
import com.tl.tutor_link.review.dto.ReviewRequestDto;
import com.tl.tutor_link.review.service.ReviewService;
import com.tl.tutor_link.user.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Review endpoints: creating a review for a completed booking, listing a
 * tutor's reviews (public), and listing the current user's own reviews.
 */
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @Valid @RequestBody ReviewRequestDto dto,
            @AuthenticationPrincipal User student
    ) {
        return ResponseEntity.ok(reviewService.createReview(dto, student));
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<ReviewDto>> getTutorReviews(@PathVariable Long tutorId) {
        return ResponseEntity.ok(reviewService.getTutorReviews(tutorId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReviewDto>> getMyReviews(
            @AuthenticationPrincipal User student
    ) {
        return ResponseEntity.ok(reviewService.getMyReviews(student));
    }

    @GetMapping("/booking/{bookingId}/exists")
    public ResponseEntity<Map<String, Boolean>> bookingHasReview(
            @PathVariable Long bookingId
    ) {
        boolean exists = reviewService.bookingHasReview(bookingId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}