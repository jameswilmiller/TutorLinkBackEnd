package com.tl.tutor_link.user.dto;

import com.tl.tutor_link.user.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Public representation of a user. Used in API responses where user details
 * need to be exposed without leaking sensitive fields like passwords or
 * verification codes
 */
@Getter
@Setter
public class UserProfileDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private Set<Role> roles;
}
