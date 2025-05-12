package com.PickOne.domain.notification.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
class RefEntityId {
    private String type;
    private Long value;

    public RefEntityId(String type, Long value) {
        validate(type, value);
        this.type = type;
        this.value = value;
    }

    private void validate(String type, Long value) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("엔티티 타입은 비어있을 수 없습니다");
        }

        if (value == null || value <= 0) {
            throw new IllegalArgumentException("엔티티 ID는 양수여야 합니다");
        }
    }
}