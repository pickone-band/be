package com.pickone.domain.notification.event;

public class ApplicationResultEvent extends BaseUserEvent {
  private final boolean approved;
  public ApplicationResultEvent(Long targetUserId, String message, boolean approved) {
    super(targetUserId, message);
    this.approved = approved;
  }
  public boolean isApproved() { return approved; }
}
