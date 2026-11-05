package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.Role;
import com.injibara.iams.backend.domain.User;
import com.injibara.iams.backend.dto.LoginRequest;
import com.injibara.iams.backend.repository.UserRepository;
import com.injibara.iams.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService authService;

    @Test
    void login_validCredentials_returnsToken() {
        Role role = new Role();
        role.setName("ADMINISTRATOR");
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash("hash");
        user.setActive(true);
        user.setRole(role);
        user.setFullName("Admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("admin123", "hash")).thenReturn(true);
        when(jwtUtil.generateToken("admin", "ADMINISTRATOR")).thenReturn("token123");
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("admin123");
        var resp = authService.login(req);
        assertEquals("token123", resp.getToken());
        assertEquals("ADMINISTRATOR", resp.getRole());
    }

    @Test
    void login_wrongPassword_throws() {
        Role role = new Role();
        role.setName("ADMINISTRATOR");
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash("hash");
        user.setActive(true);
        user.setRole(role);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("wrong");
        assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    }

    @Test
    void login_inactiveAccount_throws() {
        User user = new User();
        user.setUsername("admin");
        user.setActive(false);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("any");
        assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    }
}
