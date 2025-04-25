package com.PickOne.security.model.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public class RoleName {

    private static final Set<String> VALID_ROLE_NAMES = Set.of("ADMIN", "USER");

    private final String value;

    private RoleName(String value) {
        this.value = value;
    }

    public static RoleName of(String value) {
        if (value == null || !VALID_ROLE_NAMES.contains(value)) {
            throw new IllegalArgumentException("유효하지 않은 역할 이름입니다: " + value);
        }
        return new RoleName(value);
    }

}