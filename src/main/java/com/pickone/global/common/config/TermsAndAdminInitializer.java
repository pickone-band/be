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
      String encodedPassword = passwordEncoder.encode("admin1234");

      UserEntity admin = UserEntity.of(
          adminEmail,
          encodedPassword,
          "운영자",
          Gender.MALE,
          LocalDate.of(1990, 1, 1),
          Mbti.ENFJ,
          Collections.emptyList(),
          "서비스 운영을 담당하는 관리자입니다." // 👈 introduction 추가
      );

      admin.verify(); // 인증 처리
      admin.lock();   // 필요시 잠금

      userRepository.save(admin);
      log.info("✅ 운영자 계정 생성 완료: {}", adminEmail);
    }

    String userEmail = "user@example.com";
    if (userRepository.findByProfileEmail(userEmail).isEmpty()) {
      String encodedPassword = passwordEncoder.encode("user1234");

      UserEntity user = UserEntity.of(
          userEmail,
          encodedPassword,
          "일반사용자",
          Gender.FEMALE,
          LocalDate.of(1995, 5, 5),
          Mbti.INFP,
          Collections.emptyList(),
          "안녕하세요. 음악을 좋아하는 사용자입니다." // 👈 introduction 추가
      );

      user.verify(); // 인증 완료

      userRepository.save(user);
      log.info("✅ 일반 사용자 계정 생성 완료: {}", userEmail);
    }
  }

}
