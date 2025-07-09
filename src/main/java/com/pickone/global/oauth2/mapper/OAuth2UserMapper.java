package com.pickone.global.oauth2.mapper;

import com.pickone.domain.user.entity.User;
import com.pickone.global.oauth2.dto.OAuth2UserResponse;
import org.springframework.stereotype.Component;

@Component
public class OAuth2UserMapper {

  public OAuth2UserResponse toResponse(User user) {
    if (user == null) return null;

    var profile = user.getProfile();

    return new OAuth2UserResponse(
        profile.getEmail(),
        profile.getNickname(),
        profile.getGender(),
        profile.getBirthDate(),
        profile.getProfileImageUrl()
    );
  }
}