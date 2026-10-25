package com.smartbooking.config;

import com.smartbooking.entity.Role;
import com.smartbooking.entity.User;
import com.smartbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    @Value("${ADMIN_FULL_NAME:Platform Administrator}")
    private String adminFullName;

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
            log.info("Admin bootstrap skipped: ADMIN_EMAIL and/or ADMIN_PASSWORD not configured");
            return;
        }

        userRepository.findByEmailIgnoreCase(adminEmail.trim()).ifPresentOrElse(existing -> {
            if (existing.getRole() != Role.ADMIN) {
                existing.setRole(Role.ADMIN);
                existing.setActive(true);
                userRepository.save(existing);
                log.info("Promoted existing user to ADMIN role: {}", adminEmail);
            }
        }, () -> {
            User admin = User.builder()
                    .fullName(adminFullName)
                    .email(adminEmail.trim().toLowerCase())
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("Created bootstrap admin account for {}", adminEmail);
        });
    }
}
