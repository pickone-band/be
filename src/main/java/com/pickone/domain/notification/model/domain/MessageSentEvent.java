package com.pickone.domain.notification.model.domain;

import java.util.List;

public class MessageSentEvent extends BaseUserEvent {

  private final List<Long> recipientIds; // 여러 명
  private final String content;

  public MessageSentEvent(Long senderId, List<Long> recipientIds, String content) {
    super(senderId);
    this.recipientIds = recipientIds;
    this.content = content;
  }

  public List<Long> getRecipientIds() {
    return recipientIds;
  }

  public String getContent() {
    return content;
  }
}

