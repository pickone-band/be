package com.PickOne.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditConfig {
    // 현재 사용자 정보를 제공하는 Bean
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // 실제 구현에서는 SecurityContextHolder 등을 통해
            // 현재 인증된 사용자 정보를 반환
            return Optional.of("SYSTEM"); // 임시 하드코딩
        };
    }
}