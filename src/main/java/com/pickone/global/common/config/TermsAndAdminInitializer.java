package com.pickone.global.common.config;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.user.model.domain.Role;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.query.Term;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TermsAndAdminInitializer implements CommandLineRunner {

  private final TermJpaRepository termRepository;
  private final UserJpaRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    // 약관 초기화
    if (termRepository.count() == 0) {
      TermEntity term = new TermEntity(
          null,
          "서비스 이용약관",
          "서비스 이용에 대한 약관입니다.",
          "v1.0",
          true,
          LocalDateTime.now()
      );
      termRepository.save(term);
      log.info("✅ 약관 초기화 완료");
    }

    // 운영자 계정 초기화
    String adminEmail = "admin@example.com";
    if (userRepository.findByEmail(adminEmail).isEmpty()) {
      UserEntity admin = UserEntity.builder()
          .email(adminEmail)
          .password(passwordEncoder.encode("admin1234"))
          .nickname("운영자")
          .role(Role.ADMIN) // enum 타입일 경우
          .build();
      userRepository.save(admin);
      log.info("✅ 운영자 계정 생성 완료: {}", adminEmail);
    }
  }
}