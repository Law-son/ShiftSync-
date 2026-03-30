package com.shiftsync.app.controller;

import com.shiftsync.app.dto.ApiResponse;
import com.shiftsync.app.dto.LoginRequest;
import com.shiftsync.app.dto.LoginResponse;
import com.shiftsync.app.dto.RegisterRequest;
import com.shiftsync.app.service.AuthService;
import com.shiftsync.app.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and account management")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    public AuthController(AuthService authService, CookieUtil cookieUtil) {
        this.authService = authService;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user account",
            description = "Creates a new user with the given details. The password must meet strict security constraints.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
            }
    )
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "User registered successfully"),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    @Operation(
            summary = "Log in to the system",
            description = "Authenticates user credentials and returns JWTs via HTTP-Only cookies and JSON body.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successful authentication"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(loginResponse.getAccessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(loginResponse.getRefreshToken()).toString())
                .body(new ApiResponse<>(true, "Login successful", loginResponse));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Exchanges a valid refresh token (via HTTP-Only cookie or Authorization header) for a new set of JWTs.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
            }
    )
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Parameter(hidden = true) HttpServletRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        String refreshToken = null;

        // Try to obtain refresh token from cookies first
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        // Fallback to Authorization header
        if (refreshToken == null && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            refreshToken = authorizationHeader.substring(7);
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Refresh token is missing or empty"));
        }

        // Logic delegates to AuthService
        LoginResponse loginResponse = authService.refreshToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(loginResponse.getAccessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(loginResponse.getRefreshToken()).toString())
                .body(new ApiResponse<>(true, "Tokens refreshed successfully", loginResponse));
    }
}
