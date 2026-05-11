package com.tl.tutor_link.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Login credentials. Password is checked only for presence, not format -
 * validation rules belong on signup, not login. Differentiating the two would
 * let attackers infer password requirements.
 */
@Getter
@Setter
public class LoginUserDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
