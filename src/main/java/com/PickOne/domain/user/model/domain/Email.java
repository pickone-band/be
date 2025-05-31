package com.PickOne.domain.user.model.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
public class Email {

    private final String value;

    private Email(String value) {
        if (!value.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }
        this.value = value;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}