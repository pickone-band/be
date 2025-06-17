package com.PickOne.domain.notification.model.domain;

import java.time.LocalDateTime;

public abstract class BaseUserEvent {

    protected final Long userId;
    protected final LocalDateTime occurredAt;

    protected BaseUserEvent(Long userId) {
        this.userId = userId;
        this.occurredAt = LocalDateTime.now();
    }

    public Long getUserId() { return userId; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}

