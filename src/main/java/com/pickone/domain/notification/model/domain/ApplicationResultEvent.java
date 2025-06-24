package com.pickone.domain.notification.model.domain;

public class ApplicationResultEvent extends BaseUserEvent {

  private final Long recipientId; // 지원자 ID
  private final String recruitmentTitle;
  private final boolean accepted; // true: 수락, false: 거절

  public ApplicationResultEvent(Long senderId, Long recipientId, String recruitmentTitle,
      boolean accepted) {
    super(senderId); // 보낸 사람 = 모집자
    this.recipientId = recipientId;
    this.recruitmentTitle = recruitmentTitle;
    this.accepted = accepted;
  }

  public Long getRecipientId() {
    return recipientId;
  }

  public String getRecruitmentTitle() {
    return recruitmentTitle;
  }

  public boolean isAccepted() {
    return accepted;
  }
}
