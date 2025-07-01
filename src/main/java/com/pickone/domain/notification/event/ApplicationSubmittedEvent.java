package com.pickone.domain.notification.event;

public class ApplicationSubmittedEvent extends BaseUserEvent {
  private final Long applicationId;
  public ApplicationSubmittedEvent(Long targetUserId, String message, Long applicationId) {
    super(targetUserId, message);
    this.applicationId = applicationId;
  }
  public Long getApplicationId() { return applicationId; }
}
