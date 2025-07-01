package com.pickone.global.common.config;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.domain.Role;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.vo.UserAuthInfo;
import com.pickone.domain.user.model.vo.UserPreference;
import com.pickone.domain.user.model.vo.UserProfile;
import com.pickone.domain.user.model.vo.UserStatus;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Mbti;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class TermsAndAdminInitializer implements CommandLineRunner {

  private final TermJpaRepository termRepository;
  private final UserJpaRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    initializeTerms();
    initializeAdmin();
  }

  private void initializeTerms() {
    if (termRepository.count() == 0) {
      TermEntity term = TermEntity.create(
          "서비스 이용약관",
          "서비스 이용에 대한 약관입니다.",
          "v1.0",
          true,
          LocalDateTime.now()
      );
      termRepository.save(term);
      log.info("✅ 약관 초기화 완료");
    }
  }

  private void initializeAdmin() {
    String adminEmail = "admin@example.com";
    if (userRepository.findByProfileEmail(adminEmail).isEmpty()) {
      // 비밀번호 인코딩 분리
      String encodedPassword = passwordEncoder.encode("admin1234");

      // UserEntity.of(...) 정적 팩토리 사용
      UserEntity admin = UserEntity.of(
          adminEmail,                       // email
          encodedPassword,                  // password
          "운영자",                         // nickname
          Gender.MALE,                      // gender
          LocalDate.of(1990, 1, 1),         // birthDate
          Mbti.ENFJ,                        // mbti
          Collections.emptyList()           // genres
      );

      // 필요시 권한, 상태 등 추가 세팅
      admin.verify(); // 인증 처리
      admin.lock();   // 필요시 잠금, 아니면 생략

      userRepository.save(admin);
      log.info("✅ 운영자 계정 생성 완료: {}", adminEmail);
    }
  }
}
