package com.pickone.domain.notification.event;

import lombok.Getter;

@Getter
public abstract class BaseUserEvent {
  private final Long targetUserId;
  private final String message;

  public BaseUserEvent(Long targetUserId, String message) {
    this.targetUserId = targetUserId;
    this.message = message;
  }
}
