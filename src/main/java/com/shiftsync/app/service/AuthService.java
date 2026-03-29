package com.shiftsync.app.service;

import com.shiftsync.app.dto.RegisterRequest;
import com.shiftsync.app.entity.User;
import com.shiftsync.app.exception.DuplicateResourceException;
import com.shiftsync.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getName());
        user.setRole(request.getRole());
        user.setActive(true);

        userRepository.save(user);
    }
}
