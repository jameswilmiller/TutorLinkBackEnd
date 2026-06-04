package com.tl.tutor_link.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Email password reset request. The password reset code is the 6-digit number
 * emailed to the user after making a request.
 */
@Getter
@Setter
public class ResetPasswordDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Password code must be 6 digits")
    private String passwordCode;

    @NotBlank(message= "Password is required")
    @Size(min=8, max=100, message="Password must be longer than 8 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one uppercase letter and one number"
    )
    private String newPassword;
}
