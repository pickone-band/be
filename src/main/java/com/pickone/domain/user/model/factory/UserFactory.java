package com.pickone.domain.user.model.factory;

import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import com.pickone.domain.user.model.domain.Gender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;

@Component
public class UserFactory {

  public UserEntity create(SignupRequestDto dto, PasswordEncoder encoder) {
    String encodedPassword = encoder.encode(dto.password());

    return UserEntity.of(
        dto.email(),
        encodedPassword,
        dto.nickname(),
        dto.gender(),
        dto.birthDate(),
        null,                    // MBTI
        Collections.emptyList(),// 장르
        null                     // 👉 자기소개 없음 → null 또는 "" 등으로 설정
    );
  }

  public UserEntity createWithOAuth2(OAuth2UserInfo userInfo) {
    return UserEntity.of(
        userInfo.getEmail(),
        null,
        userInfo.getNickname(),
        Gender.MALE,
        LocalDate.now(),
        null,
        Collections.emptyList(),
        "안녕하세요! 소셜 가입 사용자입니다." // 기본 소개글
    );
  }
}
