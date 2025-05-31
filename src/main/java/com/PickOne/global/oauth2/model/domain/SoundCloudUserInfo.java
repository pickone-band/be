package com.PickOne.global.oauth2.model.domain;

import java.util.Map;

public class SoundCloudUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public SoundCloudUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
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
    public String getNickname() {
        return (String) attributes.getOrDefault("username", "");
    }
}