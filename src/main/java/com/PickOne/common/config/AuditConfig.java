package com.PickOne.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Bean
    @Profile("test")
    public AuditorAware<String> testAuditorProvider() {
        return () -> Optional.of("TEST_USER");
    }

    @Bean
    @Profile("!test")
    public AuditorAware<String> productionAuditorProvider() {
        return () -> Optional.of("SYSTEM_USER");
    }
}