package com.tl.tutor_link.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Returned to the client after successful login or token refresh.
 * The refresh token is delivered separately as an httpOnly cookie,
 * not in this payload, to prevent XSS theft.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

    private String accessToken;
    private long accessTokenExpiresIn;
}
