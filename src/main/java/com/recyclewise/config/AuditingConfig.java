package com.recyclewise.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.springframework.lang.NonNull;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

    @Bean
    @NonNull
    public AuditorAware<String> auditorProvider() {
        return new AuditorAware<String>() {
            @Override
            @NonNull
            @SuppressWarnings("null")
            public Optional<String> getCurrentAuditor() {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpSession session = attributes.getRequest().getSession(false);
                    if (session != null) {
                        String username = (String) session.getAttribute("username");
                        if (username != null) {
                            return Optional.of(username);
                        }
                    }
                }
                return Optional.of("SYSTEM");
            }
        };
    }
}
