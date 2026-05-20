package com.tl.tutor_link.booking.controller;

import com.tl.tutor_link.booking.dto.BookingDto;
import com.tl.tutor_link.booking.dto.BookingRequestDto;
import com.tl.tutor_link.booking.dto.UpdateMeetingLinkRequestDto;
import com.tl.tutor_link.booking.dto.UpdateMeetingLocationRequestDto;
import com.tl.tutor_link.booking.service.BookingService;
import com.tl.tutor_link.user.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Booking endpoints: creating a booking, listing a user's bookings (as
 * student or as tutor), and the status transitions (accept, decline,
 * cancel, complete). Authorization for each transition is enforced in
 * {@link BookingService}.
 */
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @Valid @RequestBody BookingRequestDto dto,
            @AuthenticationPrincipal User student
    ) {
        return ResponseEntity.ok(bookingService.createBooking(dto, student));
    }

    @GetMapping("/me/student")
    public ResponseEntity<List<BookingDto>> getMyStudentBookings(
            @AuthenticationPrincipal User student
    ) {
        return ResponseEntity.ok(bookingService.getStudentBookings(student));
    }

    @GetMapping("/me/tutor")
    public ResponseEntity<List<BookingDto>> getMyTutorBookings(
            @AuthenticationPrincipal User tutorUser
    ) {
        return ResponseEntity.ok(bookingService.getTutorBookings(tutorUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(
            @PathVariable Long id,
            @AuthenticationPrincipal User caller
    ) {
        return ResponseEntity.ok(bookingService.getBookingById(id, caller));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<BookingDto> acceptBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal User tutorUser
    ) {
        return ResponseEntity.ok(bookingService.acceptBooking(id, tutorUser));
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<BookingDto> declineBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal User tutorUser
    ) {
        return ResponseEntity.ok(bookingService.declineBooking(id, tutorUser));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal User caller
    ) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, caller));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<BookingDto> completeBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal User tutorUser
    ) {
        return ResponseEntity.ok(bookingService.completeBooking(id, tutorUser));
    }

    @PatchMapping("/{id}/meeting-link")
    public ResponseEntity<BookingDto> updateMeetingLink(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMeetingLinkRequestDto request,
            @AuthenticationPrincipal User tutorUser
    ) {
        return ResponseEntity.ok(
                bookingService.updateMeetingLink(id, request.getMeetingLink(), tutorUser)
        );
    }

    @PatchMapping("/{id}/meeting-location")
    public ResponseEntity<BookingDto> updateMeetingLocation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMeetingLocationRequestDto request,
            @AuthenticationPrincipal User tutorUser
    ) {
        return ResponseEntity.ok(
                bookingService.updateMeetingLocation(id, request.getMeetingLocation(), tutorUser)
        );
    }
}