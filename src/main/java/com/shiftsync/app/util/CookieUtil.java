package com.shiftsync.app.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private final boolean secure;
    private final String sameSite;
    private final long jwtExpiration;
    private final long refreshExpiration;

    public CookieUtil(
            @Value("${application.security.cookie.secure:false}") boolean secure,
            @Value("${application.security.cookie.same-site:Strict}") String sameSite,
            @Value("${application.security.jwt.expiration:86400000}") long jwtExpiration,
            @Value("${application.security.jwt.refresh-token.expiration:604800000}") long refreshExpiration) {
        this.secure = secure;
        this.sameSite = sameSite;
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(jwtExpiration / 1000) // Convert ms to seconds
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/api/auth/refresh") // Restrict refresh token cookie path
                .maxAge(refreshExpiration / 1000) // Convert ms to seconds
                .build();
    }

    public ResponseCookie deleteAccessTokenCookie() {
        return ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(0)
                .build();
    }

    public ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/api/auth/refresh")
                .maxAge(0)
                .build();
    }
}
