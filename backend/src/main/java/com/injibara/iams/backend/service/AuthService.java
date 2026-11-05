package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.Role;
import com.injibara.iams.backend.domain.User;
import com.injibara.iams.backend.dto.LoginRequest;
import com.injibara.iams.backend.dto.LoginResponse;
import com.injibara.iams.backend.repository.UserRepository;
import com.injibara.iams.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is inactive");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);
        String role = user.getRole().getName();
        String token = jwtUtil.generateToken(user.getUsername(), role);
        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(role)
                .build();
    }
}
