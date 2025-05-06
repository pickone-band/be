package com.PickOne.domain.term.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Version {
    private static final int MAX_LENGTH = 20;

    private String value;

    private Version(String value) {
        this.value = value;
    }

    public static Version of(String value) {
        validate(value);
        return new Version(value);
    }

    private static void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("버전은 비어있을 수 없습니다.");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("버전은 %d자를 초과할 수 없습니다.", MAX_LENGTH));
        }
    }

    public boolean isNewerThan(Version other) {
        return this.value.compareTo(other.value) > 0;
    }
}