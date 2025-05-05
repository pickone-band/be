package com.PickOne.term.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class EffectiveDate {

    private LocalDateTime value;

    private EffectiveDate(LocalDateTime value) {
        this.value = value;
    }

    public static EffectiveDate of(LocalDateTime value) {
        validate(value);
        return new EffectiveDate(value);
    }

    private static void validate(LocalDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("시행일은 null일 수 없습니다.");
        }
    }

    //유효한 기간 검증해주는 메서드(유효기간 이전 날짜 True, 동일한 날짜 True, 이후의 날짜 False)
    public boolean isEffectiveAt(LocalDateTime date) {
        return date.isBefore(value) || date.isEqual(value);
    }
}

