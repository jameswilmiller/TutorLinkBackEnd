package com.tl.tutor_link.availability.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Dates in a requested range that have at least one open slot, so the
 * booking calendar can disable the rest.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookableDatesDto {

    private boolean hasAvailability;
    private List<String> dates;
}
