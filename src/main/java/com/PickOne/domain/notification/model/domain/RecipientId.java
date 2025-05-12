package com.PickOne.domain.notification.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Value object for Recipient ID
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
class RecipientId {
    private Long value;

    public RecipientId(Long value) {
        validate(value);
        this.value = value;
    }

    private void validate(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("수신자 ID는 양수여야 합니다");
        }
    }
}
