package com.pickone.global.oauth2.model.domain;

import com.pickone.global.common.enums.Mbti;
import java.util.Map;

public class SoundCloudUserInfo extends AbstractOAuth2UserInfo {


  public SoundCloudUserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    return String.valueOf(attributes.get("id"));
  }

  @Override
  public String getEmail() {
    return (String) attributes.get("email");
  }

  @Override
  public String getIntroduction() {
    return "";
  }

  @Override
  public Mbti getMbti() {
    return null;
  }

}