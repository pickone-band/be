package com.PickOne.global.oauth2.model.domain;

import java.util.List;
import java.util.Map;

public class SpotifyUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public SpotifyUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
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
