package com.shiftsync.app.service;

import com.shiftsync.app.dto.LoginRequest;
import com.shiftsync.app.dto.LoginResponse;
import com.shiftsync.app.dto.RegisterRequest;
import com.shiftsync.app.entity.Employee;
import com.shiftsync.app.entity.User;
import com.shiftsync.app.exception.DuplicateResourceException;
import com.shiftsync.app.exception.InvalidCredentialsException;
import com.shiftsync.app.exception.UserNotFoundException;
import com.shiftsync.app.repository.UserRepository;
import com.shiftsync.app.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setActive(true);

        Employee employee = new Employee();
        employee.setFullName(request.getName());
        employee.setUser(user);
        user.setEmployee(employee);

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        // Enforce distinguishing errors as requested by the user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect password");
        }

        // Properly authenticate standard way to set up standard logging if any filters rely on it
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(
                user.getId(),
                user.getEmployee() != null ? user.getEmployee().getFullName() : user.getEmail(),
                user.getRole(),
                accessToken,
                refreshToken
        );
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        String userEmail = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User associated with refresh token not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new InvalidCredentialsException("Invalid refresh token for user");
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(
                user.getId(),
                user.getEmployee() != null ? user.getEmployee().getFullName() : user.getEmail(),
                user.getRole(),
                newAccessToken,
                newRefreshToken
        );
    }
}
