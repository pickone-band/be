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
        null,          // MBTI: 현재 dto에 없음
        Collections.emptyList()           // 장르: 현재 dto에 없음
    );
  }

  public UserEntity createWithOAuth2(OAuth2UserInfo userInfo) {
    return UserEntity.of(
        userInfo.getEmail(),
        null,  // 소셜 로그인은 비밀번호 없음
        userInfo.getNickname(),
        Gender.MALE,             // 기본값 (또는 Gender.UNKNOWN 등 정의 가능)
        LocalDate.now(),         // 생일 정보 없음
        null,
        Collections.emptyList()
    );
  }
}
