package com.tl.tutor_link.booking.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Body for PATCH /bookings/{id}/meeting-location.
 */
@Getter
@Setter
public class UpdateMeetingLocationRequestDto {

    @Size(max = 500, message = "Meeting location must be at most 500 characters")
    private String meetingLocation;
}