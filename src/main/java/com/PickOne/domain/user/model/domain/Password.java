package com.PickOne.domain.user.model.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@EqualsAndHashCode
@Embeddable
public class Password {

    private String value;

    protected Password() {}

    public Password(String value) {
        this.value = value;
    }

    public static Password ofRaw(String raw, PasswordEncoder encoder) {
        if (raw == null || raw.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }

        return new Password(encoder.encode(raw));
    }

    public static Password ofEncoded(String encodedPassword) {
        return new Password(encodedPassword);
    }

    public boolean matches(String raw, PasswordEncoder encoder) {
        return encoder.matches(raw, value);
    }

}