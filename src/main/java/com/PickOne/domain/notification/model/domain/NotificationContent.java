package com.PickOne.domain.notification.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Value object for Notification content
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
class NotificationContent {
    private String value;

    public NotificationContent(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("알림 내용은 비어있을 수 없습니다");
        }

        if (value.length() > 255) {
            throw new IllegalArgumentException("알림 내용은 255자를 초과할 수 없습니다");
        }
    }
}