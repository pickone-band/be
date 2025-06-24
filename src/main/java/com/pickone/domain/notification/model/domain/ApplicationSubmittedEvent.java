package com.pickone.domain.notification.model.domain;

public class ApplicationSubmittedEvent extends BaseUserEvent {

  private final Long receiverId;
  private final String recruitmentTitle;

  public ApplicationSubmittedEvent(Long applicantId, Long receiverId, String recruitmentTitle) {
    super(applicantId);
    this.receiverId = receiverId;
    this.recruitmentTitle = recruitmentTitle;
  }

  public Long getReceiverId() {
    return receiverId;
  }

  public String getRecruitmentTitle() {
    return recruitmentTitle;
  }
}