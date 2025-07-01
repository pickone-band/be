package com.pickone.domain.notification.event;

public class FollowedUserEvent extends BaseUserEvent {
  public FollowedUserEvent(Long targetUserId, String message) {
    super(targetUserId, message);
  }
}
