package com.tl.tutor_link.availability.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Bookable start times for one date. {@code hasAvailability} is false when
 * the tutor has set no hours at all, which lets the client fall back to
 * free-form time entry rather than showing an empty picker.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpenSlotsDto {

    private boolean hasAvailability;
    private List<String> slots;
}
