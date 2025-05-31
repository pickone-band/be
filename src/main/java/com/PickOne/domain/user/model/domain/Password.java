package com.PickOne.domain.user.model.domain;

import com.PickOne.global.security.config.PasswordEncoder;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Password {

    private final String value;

    private Password(String value) {
        this.value = value;
    }

    public static Password ofRaw(String rawPassword, PasswordEncoder encoder) {
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
        // 해싱은 AuthService에서 처리
        return new Password(encoder.encode(rawPassword));
    }

    public static Password ofEncoded(String encodedPassword) {
        return new Password(encodedPassword);
    }

    public boolean matches(String raw, PasswordEncoder encoder) {
        return encoder.matches(raw, value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}