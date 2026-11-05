package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.Role;
import com.injibara.iams.backend.domain.User;
import com.injibara.iams.backend.repository.RoleRepository;
import com.injibara.iams.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> all() { return userRepository.findAll(); }

    public User create(String username, String password, String fullName, String email, String roleName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        User user = User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName)
                .email(email)
                .role(role)
                .active(true)
                .build();
        return userRepository.save(user);
    }

    public User update(Long id, String fullName, String email, String roleName, Boolean active) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (fullName != null) user.setFullName(fullName);
        if (email != null) user.setEmail(email);
        if (roleName != null) {
            Role role = roleRepository.findByName(roleName).orElseThrow(() -> new IllegalArgumentException("Role not found"));
            user.setRole(role);
        }
        if (active != null) user.setActive(active);
        return userRepository.save(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) throw new IllegalArgumentException("User not found");
        userRepository.deleteById(id);
    }
}
