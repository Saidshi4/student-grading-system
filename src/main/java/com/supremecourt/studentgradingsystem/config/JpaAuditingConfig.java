package com.supremecourt.studentgradingsystem.config;

import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("SYSTEM");
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserEntity user && user.getUsername() != null) {
                return Optional.of(user.getUsername());
            }
            String name = authentication.getName();
            if (name == null || name.isBlank() || "anonymousUser".equals(name)) {
                return Optional.of("SYSTEM");
            }
            return Optional.of(name);
        };
    }
}
