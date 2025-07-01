package com.pickone.domain.notification.event;

public class RecruitmentCreatedEvent extends BaseUserEvent {
  private final Long recruitmentId;
  public RecruitmentCreatedEvent(Long targetUserId, String message, Long recruitmentId) {
    super(targetUserId, message);
    this.recruitmentId = recruitmentId;
  }
  public Long getRecruitmentId() { return recruitmentId; }
}
