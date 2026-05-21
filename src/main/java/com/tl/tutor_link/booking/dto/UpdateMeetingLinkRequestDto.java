package com.tl.tutor_link.booking.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Body for PATCH /bookings/{id}/meeting-link.
 * The meeting link is optional — passing null/blank clears it.
 */
@Getter
@Setter
public class UpdateMeetingLinkRequestDto {

    @Size(max = 500, message = "Meeting link must be at most 500 characters")
    private String meetingLink;
}