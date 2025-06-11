package com.PickOne.global.oauth2.model.domain;

import com.PickOne.domain.user.model.domain.Gender;

import java.time.LocalDate;
import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
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

    @Override
    public String getProfileImageUrl() {
        return (String) attributes.getOrDefault("profile_image", null);
    }

    @Override
    public Gender getGender() {
        String gender = (String) attributes.get("gender");
        if ("male".equalsIgnoreCase(gender)) return Gender.MALE;
        if ("female".equalsIgnoreCase(gender)) return Gender.FEMALE;
        return Gender.MALE; // 기본값
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