package com.tl.tutor_link.dto;

import com.tl.tutor_link.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserProfileDto {
    private long id;
    private String username;
    private String email;
    private Set<Role> roles;
}
