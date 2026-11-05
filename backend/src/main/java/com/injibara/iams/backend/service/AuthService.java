package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.Role;
import com.injibara.iams.backend.domain.User;
import com.injibara.iams.backend.dto.LoginRequest;
import com.injibara.iams.backend.dto.LoginResponse;
import com.injibara.iams.backend.repository.RoleRepository;
import com.injibara.iams.backend.repository.UserRepository;
import com.injibara.iams.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final RoleRepository roleRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
    }

    public LoginResponse register(String username, String password, String fullName, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        Role role = roleRepository.findByName("VIEWER")
                .orElseThrow(() -> new IllegalArgumentException("Default role not found"));
        User user = User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName)
                .email(email)
                .role(role)
                .active(true)
                .build();
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername(), role.getName());
        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(role.getName())
                .build();
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
