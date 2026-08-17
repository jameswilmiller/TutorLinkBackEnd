package com.tl.tutor_link.availability.model;

import com.tl.tutor_link.tutor.model.Tutor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * A weekly recurring window in which a tutor accepts bookings,
 * e.g. every Monday 09:00-12:00. A tutor may have several per day.
 */
@Entity
@Table(name = "availability_rules")
@Getter
@Setter
public class AvailabilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;
}
