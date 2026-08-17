package com.tl.tutor_link.availability.controller;

import com.tl.tutor_link.availability.dto.AvailabilityRuleDto;
import com.tl.tutor_link.availability.dto.BookableDatesDto;
import com.tl.tutor_link.availability.dto.OpenSlotsDto;
import com.tl.tutor_link.availability.service.AvailabilityService;
import com.tl.tutor_link.user.model.User;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Availability endpoints: tutors manage their weekly hours, students fetch
 * the open slots for a given date and duration while booking.
 */
@RestController
@RequestMapping("/tutors")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/me/availability")
    public ResponseEntity<List<AvailabilityRuleDto>> getMyAvailability(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(availabilityService.getMyRules(user));
    }

    @PutMapping("/me/availability")
    public ResponseEntity<List<AvailabilityRuleDto>> replaceMyAvailability(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody List<@Valid AvailabilityRuleDto> rules
    ) {
        return ResponseEntity.ok(availabilityService.replaceMyRules(user, rules));
    }

    @GetMapping("/{tutorId}/availability/days")
    public ResponseEntity<BookableDatesDto> getBookableDates(
            @PathVariable Long tutorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam int durationMinutes
    ) {
        return ResponseEntity.ok(
                availabilityService.getBookableDates(tutorId, from, to, durationMinutes));
    }

    @GetMapping("/{tutorId}/availability/slots")
    public ResponseEntity<OpenSlotsDto> getOpenSlots(
            @PathVariable Long tutorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam int durationMinutes
    ) {
        return ResponseEntity.ok(availabilityService.getOpenSlots(tutorId, date, durationMinutes));
    }
}
