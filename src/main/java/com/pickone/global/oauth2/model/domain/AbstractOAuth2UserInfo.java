package com.pickone.global.oauth2.model.domain;

import com.pickone.domain.user.model.domain.Gender;

import java.time.LocalDate;
import java.util.Map;

public abstract class AbstractOAuth2UserInfo implements OAuth2UserInfo {

  protected final Map<String, Object> attributes;

  protected AbstractOAuth2UserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String getNickname() {
    return (String) attributes.getOrDefault("username", "");
  }

  @Override
  public String getProfileImageUrl() {
    return (String) attributes.getOrDefault("profile_image", null);
  }

  @Override
  public Gender getGender() {
    String gender = (String) attributes.get("gender");
      if ("male".equalsIgnoreCase(gender)) {
          return Gender.MALE;
      }
      if ("female".equalsIgnoreCase(gender)) {
          return Gender.FEMALE;
      }
    return Gender.MALE; // default value
  }

  @Override
  public LocalDate getBirthDate() {
    String birth = (String) attributes.get("birth_date");
    try {
      return birth != null ? LocalDate.parse(birth) : LocalDate.of(2000, 1, 1);
    } catch (Exception e) {
      return LocalDate.of(2000, 1, 1);
    }
  }
}

