package com.tl.tutor_link.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
/**
 * Builds and reads the refresh-token cookie. Refresh tokens are httpOnly so
 * they can't be read by JavaScript (XSS protection), and the secure flag is
 * driven by config so the cookie can be sent over plain HTTP in dev but
 * requires HTTPS in production.
 */
@Service
public class CookieService {

    @Value("${security.refresh.cookie-name}")
    private String refreshCookieName;

    @Value("${security.refresh.cookie-secure}")
    private boolean secureCookie;

    public ResponseCookie createRefreshTokenCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(refreshCookieName, token)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }

    public ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }

    public String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (refreshCookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
