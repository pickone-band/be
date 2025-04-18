package com.PickOne.term.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Title {

    private static final int MAX_LENGTH = 100;

    private String value;

    private Title(String value) {
        this.value = value;
    }

    public static Title of(String value) {
        validate(value);
        return new Title(value);
    }

    private static void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 비어있을 수 없습니다.");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("제목은 %d자를 초과할 수 없습니다.", MAX_LENGTH));
        }
    }
}