package com.pickone.global.oauth2.model.domain;

import com.pickone.domain.user.model.domain.Gender;

import com.pickone.global.common.enums.Mbti;
import java.time.LocalDate;
import java.util.Map;

public interface OAuth2UserInfo {

  String getId();

  String getEmail();

  String getNickname();

  String getProfileImageUrl();

  Gender getGender();

  LocalDate getBirthDate();

  String getIntroduction();
  Mbti getMbti();

  static OAuth2UserInfo of(OAuth2Provider provider, Map<String, Object> attributes) {
    return switch (provider) {
      case GOOGLE -> new GoogleUserInfo(attributes);
      case SPOTIFY -> new SpotifyUserInfo(attributes);
      case SOUNDCLOUD -> new SoundCloudUserInfo(attributes);
      case INSTAGRAM -> new InstagramUserInfo(attributes);
    };
  }
}