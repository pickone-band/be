package com.PickOne.term.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Content {
    private String value;

    private Content(String value) {
        this.value = value;
    }

    public static Content of(String value) {
        validate(value);
        return new Content(value);
    }

    private static void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 비어있을 수 없습니다.");
        }
    }
}