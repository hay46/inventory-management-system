package com.injibara.iams.backend.controller;

import com.injibara.iams.backend.domain.User;
import com.injibara.iams.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<User>> all() { return ResponseEntity.ok(service.all()); }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<User> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.create(
                body.get("username"), body.get("password"), body.get("fullName"),
                body.get("email"), body.get("role")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.update(id,
                (String) body.get("fullName"), (String) body.get("email"),
                (String) body.get("role"), (Boolean) body.get("active")));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
