package com.pickone.domain.user.factory;

import com.pickone.domain.user.dto.SignupRequest;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.entity.User;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;

@Component
public class UserFactory {

  public User create(SignupRequest dto, PasswordEncoder encoder) {
    return User.create(
        dto.email(),
        encoder.encode(dto.password()),
        dto.nickname(),
        dto.gender(),
        dto.birthDate(),
        null,
        Collections.emptyList(),
        null,
        null // ✅ profileImageUrl
    );
  }

  public User createWithOAuth2(OAuth2UserInfo userInfo) {
    return User.create(
        userInfo.getEmail(),
        null,
        userInfo.getNickname(),
        userInfo.getGender(),                 // ✅ Gender from OAuth2 info
        userInfo.getBirthDate(),              // ✅ LocalDate from OAuth2 info
        userInfo.getMbti(),                   // ✅ Optional
        Collections.emptyList(),
        userInfo.getIntroduction(),           // ✅ Optional
        userInfo.getProfileImageUrl()         // ✅ ✅ 추가된 필드
    );
  }
}
