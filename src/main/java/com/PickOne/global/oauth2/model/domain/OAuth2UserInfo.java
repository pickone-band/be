package com.PickOne.global.oauth2.model.domain;

import com.PickOne.domain.user.model.domain.Gender;

import java.time.LocalDate;
import java.util.Map;

public interface OAuth2UserInfo {
    String getId();
    String getEmail();
    String getNickname();
    String getProfileImageUrl();
    Gender getGender();
    LocalDate getBirthDate();

    static OAuth2UserInfo of(OAuth2Provider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> new GoogleUserInfo(attributes);
            case SPOTIFY -> new SpotifyUserInfo(attributes);
            case SOUNDCLOUD -> new SoundCloudUserInfo(attributes);
            case INSTAGRAM -> new InstagramUserInfo(attributes);
        };
    }
}