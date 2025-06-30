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
    if (userRepository.findByProfile_Email(adminEmail).isEmpty()) {
      UserEntity admin = UserEntity.builder()
          .profile(
              UserProfile.of(
                  adminEmail,
                  passwordEncoder.encode("admin1234"),
                  "운영자",
                  LocalDate.of(1990, 1, 1),
                  Gender.MALE,
                  Mbti.ENFJ, // 혹은 기본값
                  null // 프로필 이미지 등
              )
          )
          .status(
              UserStatus.init().activate().verify() // 활성화, 인증 상태 true로 세팅
          )
          .preference(
              UserPreference.ofNullable(Collections.emptyList())
          )
          .authInfo(
              UserAuthInfo.of(false) // 예: oauth false, 추가 인증정보 true
          )
          .role(Role.ADMIN)
          .build();

      userRepository.save(admin);
      log.info("✅ 운영자 계정 생성 완료: {}", adminEmail);
    }
  }
}
