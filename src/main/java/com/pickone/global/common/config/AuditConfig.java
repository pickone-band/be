package com.pickone.global.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

  public AuditorAware<String> productionAuditorProvider() {
    // 운영 환경에서는 현재 인증된 사용자 반환
    return () -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null
          && authentication.isAuthenticated()
          && authentication.getPrincipal() instanceof String) {
        return Optional.of((String) authentication.getPrincipal());
      }
      // 인증되지 않은 경우 시스템 사용자로 설정
      return Optional.of("SYSTEM_USER");
    };
  }
}