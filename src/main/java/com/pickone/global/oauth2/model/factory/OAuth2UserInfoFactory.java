package com.pickone.global.oauth2.model.factory;

import com.pickone.global.oauth2.model.domain.GoogleUserInfo;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import com.pickone.global.oauth2.model.domain.SoundCloudUserInfo;
import com.pickone.global.oauth2.model.domain.SpotifyUserInfo;
import java.util.Map;

public class OAuth2UserInfoFactory {
  public static OAuth2UserInfo create(String provider, Map<String, Object> attributes) {
    return switch (provider.toLowerCase()) {
      case "google" -> new GoogleUserInfo(attributes);
      case "spotify" -> new SpotifyUserInfo(attributes);
      case "soundcloud" -> new SoundCloudUserInfo(attributes);// 추가
      default -> throw new IllegalArgumentException("Unknown OAuth2 provider: " + provider);
    };
  }
}