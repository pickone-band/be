package com.PickOne.global.oauth2.model.domain;

import lombok.Getter;

@Getter
public enum OAuth2Provider {
    GOOGLE("Google");

    private final String value;

    OAuth2Provider(String value) {
        this.value = value;
    }
}