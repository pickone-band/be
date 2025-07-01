package com.pickone.domain.user.model.factory;

import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import java.time.LocalDate;
import java.util.Collections;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
        dto.mbti(),
        dto.genres()
    );
  }

  public UserEntity createWithOAuth2(OAuth2UserInfo userInfo) {
    // Provider/ProviderId/이미지 등 필요시 추가 세팅
    return UserEntity.of(
        userInfo.getEmail(),
        null,  // 소셜 가입은 패스워드 없음
        userInfo.getNickname(),
        Gender.MALE,            // 소셜에서는 일단 UNKNOWN
        LocalDate.now(),           // 소셜에는 생년월일이 없을 수 있음
        null,                      // MBTI, 필요시 추출/입력
        Collections.emptyList()    // 장르, 필요시 추가 입력
    );
  }
}
