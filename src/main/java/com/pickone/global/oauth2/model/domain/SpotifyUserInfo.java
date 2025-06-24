package com.pickone.global.oauth2.model.domain;

import java.util.List;
import java.util.Map;

public class SpotifyUserInfo extends AbstractOAuth2UserInfo {

  public SpotifyUserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    return (String) attributes.get("id");
  }

  @Override
  public String getEmail() {
    Map<String, Object> emailObj = ((List<Map<String, Object>>) attributes.get("emails")).get(0);
    return (String) emailObj.get("value");
  }

  @Override
  public String getNickname() {
    return (String) attributes.getOrDefault("display_name", "");
  }
}
