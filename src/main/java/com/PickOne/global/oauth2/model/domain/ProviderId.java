package com.PickOne.global.oauth2.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ProviderId {
    private String value;

    private ProviderId(String providerId) {
        this.value = providerId;
    }

    public static ProviderId of(String providerId) {
        if (providerId == null || providerId.trim().isEmpty()) {
            throw new IllegalArgumentException("제공자 ID는 빈 값일 수 없습니다.");
        }
        return new ProviderId(providerId);
    }
}