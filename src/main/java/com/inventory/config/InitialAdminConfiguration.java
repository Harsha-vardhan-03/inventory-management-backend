package com.inventory.config;

import com.inventory.common.enums.Role;
import com.inventory.modules.user.entities.User;
import com.inventory.modules.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class InitialAdminConfiguration {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner createInitialSuperAdmin(
            @Value("${initial-super-admin.email:admin@inventory.com}") String email,
            @Value("${initial-super-admin.password}") String password) {
        return args -> {
            if (userRepository.existsByEmail(email)) {
                return;
            }

            userRepository.save(User.builder()
                    .name("Super Admin")
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.SUPER_ADMIN)
                    .isActive(true)
                    .isPasswordResetRequired(false)
                    .build());
        };
    }
}
