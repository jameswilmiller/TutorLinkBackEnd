package com.tl.tutor_link.booking.model;

import java.util.Set;

/**
 * Lifecycle state of a booking, with the allowed transitions between states.
 *
 *   PENDING   → ACCEPTED, DECLINED, CANCELLED
 *   ACCEPTED  → CANCELLED, COMPLETED
 *   DECLINED, CANCELLED, COMPLETED → (terminal, no transitions)
 *
 * Transition rules live here so the full state machine can be understood
 * from one place rather than scattered across the service.
 */
public enum BookingStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    CANCELLED,
    COMPLETED;

    private Set<BookingStatus> allowedTransitions;

    static {
        PENDING.allowedTransitions = Set.of(ACCEPTED, DECLINED, CANCELLED);
        ACCEPTED.allowedTransitions = Set.of(CANCELLED, COMPLETED);
        DECLINED.allowedTransitions = Set.of();
        CANCELLED.allowedTransitions = Set.of();
        COMPLETED.allowedTransitions = Set.of();
    }

    /**
     * Returns true if a booking in this state is allowed to move to the
     * given target state.
     */
    public boolean canTransitionTo(BookingStatus target) {
        return allowedTransitions.contains(target);
    }
}