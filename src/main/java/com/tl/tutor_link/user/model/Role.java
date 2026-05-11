package com.tl.tutor_link.user.model;

/**
 * Roles assignable to a user. STUDENT is granted on signup. TUTOR is added
 * when a user creates a tutor profile. ADMIN is reserved for internal use.
 */
public enum Role {
    STUDENT,
    TUTOR,
    ADMIN
}
