package com.PickOne.domain.messaging.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
class MessageContent {
    private String value;

    public MessageContent(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용은 비어있을 수 없습니다");
        }

        if (value.length() > 2000) {
            throw new IllegalArgumentException("메시지 내용은 2000자를 초과할 수 없습니다");
        }
    }
}