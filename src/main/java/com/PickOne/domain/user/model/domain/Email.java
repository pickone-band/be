package com.PickOne.domain.user.model.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class Email {

    private String value;

    protected Email() {}

    public Email(String value) {
        if (!value.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }
        this.value = value;
    }

    public static Email of(String value) {
        return new Email(value);
    }
}