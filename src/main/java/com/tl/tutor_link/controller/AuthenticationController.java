package com.tl.tutor_link.controller;

import com.tl.tutor_link.dto.AuthResponseDto;
import com.tl.tutor_link.dto.LoginUserDto;
import com.tl.tutor_link.dto.RegisterUserDto;
import com.tl.tutor_link.dto.VerifyUserDto;
import com.tl.tutor_link.model.RefreshToken;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.service.AuthenticationService;
import com.tl.tutor_link.service.CookieService;
import com.tl.tutor_link.service.JwtService;
import com.tl.tutor_link.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private RefreshTokenService refreshTokenService;
    private final CookieService cookieService;

    public AuthenticationController(
            JwtService jwtService,
            AuthenticationService authenticationService,
            RefreshTokenService refreshTokenService,
            CookieService cookieService
    ) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.cookieService = cookieService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        authenticationService.signup(registerUserDto);
        return ResponseEntity.ok("User registered succesfully. please verify your email");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String accessToken = jwtService.generateAccessToken(authenticatedUser);

        refreshTokenService.revokeAllUserTokens(authenticatedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser);

        long refreshMaxAgeSeconds = jwtService.getRefreshTokenExpiration() / 1000;

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookieService.createRefreshTokenCookie(
                                refreshToken.getToken(),
                                refreshMaxAgeSeconds).toString()
                ).body(new AuthResponseDto(
                        accessToken,
                        jwtService.getAccessTokenExpiration()
                ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(HttpServletRequest request) {
        String refreshTokenValue = cookieService.extractRefreshTokenFromCookies(request);

        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        RefreshToken storedToken = refreshTokenService.validateStoredRefreshToken(refreshTokenValue);
        User user = storedToken.getUser();

        if (!jwtService.isTokenValidAndOfType(refreshTokenValue, user, "refresh")) {
            return ResponseEntity.status(401).build();
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(
                new AuthResponseDto(
                        newAccessToken,
                        jwtService.getAccessTokenExpiration()
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String refreshTokenValue = cookieService.extractRefreshTokenFromCookies(request);

        if (refreshTokenValue != null && !refreshTokenValue.isBlank()) {
            refreshTokenService.revokeToken(refreshTokenValue);
        }
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookieService.clearRefreshTokenCookie().toString()
                )
                .body("Logged out successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code resent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


