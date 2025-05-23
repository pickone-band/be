package com.PickOne.domain.messaging.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
class SenderId {
    private Long value;

    public SenderId(Long value) {
        validate(value);
        this.value = value;
    }

    private void validate(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("발신자 ID는 양수여야 합니다");
        }
    }
}