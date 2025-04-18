package com.PickOne.term.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class EffectiveDate {
    private LocalDate value;

    private EffectiveDate(LocalDate value) {
        this.value = value;
    }

    public static EffectiveDate of(LocalDate value) {
        validate(value);
        return new EffectiveDate(value);
    }

    private static void validate(LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("시행일은 null일 수 없습니다.");
        }
    }

    public boolean isEffectiveAt(LocalDate date) {
        return !date.isBefore(value);
    }
}

