package com.injibara.iams.backend.controller;

import com.injibara.iams.backend.dto.LoginRequest;
import com.injibara.iams.backend.dto.LoginResponse;
import com.injibara.iams.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody java.util.Map<String, String> body) {
        return ResponseEntity.ok(authService.register(
                body.get("username"), body.get("password"),
                body.get("fullName"), body.get("email")));
    }
}
