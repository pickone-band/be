package com.pickone.domain.user.model.policy;

import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserPolicy {

  public void validateSignup(SignupRequestDto dto) {
    if (dto.email() == null || dto.password() == null || dto.nickname() == null) {
      throw new IllegalArgumentException("필수값 누락");
    }
    // 추가 정책/중복 체크 등 구현
  }

  public void validateSignupWithOAuth2(OAuth2UserInfo userInfo) {
    if (userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
      throw new IllegalArgumentException("소셜 로그인 정보에 이메일이 없습니다.");
    }
    if (userInfo.getNickname() == null || userInfo.getNickname().isBlank()) {
      throw new IllegalArgumentException("소셜 로그인 정보에 닉네임이 없습니다.");
    }
  }
}