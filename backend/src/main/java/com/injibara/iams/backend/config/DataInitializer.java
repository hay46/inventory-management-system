package com.injibara.iams.backend.config;

import com.injibara.iams.backend.domain.Role;
import com.injibara.iams.backend.domain.User;
import com.injibara.iams.backend.repository.RoleRepository;
import com.injibara.iams.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createRoleIfMissing("ADMINISTRATOR", "Full system access");
        createRoleIfMissing("INVENTORY_MANAGER", "Manages inventory and stock");
        createRoleIfMissing("STOREKEEPER", "Records stock transactions");
        createRoleIfMissing("VIEWER", "Read-only access");

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .email("admin@iams.local")
                    .role(roleRepository.findByName("ADMINISTRATOR").orElseThrow())
                    .active(true)
                    .build();
            userRepository.save(admin);
        }
    }

    private void createRoleIfMissing(String name, String desc) {
        if (roleRepository.findByName(name).isEmpty()) {
            roleRepository.save(Role.builder().name(name).description(desc).build());
        }
    }
}
