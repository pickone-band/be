package com.pickone.domain.notification.event;

import java.util.List;

public class MessageSentEvent {
  private final Long senderId;
  private final List<Long> recipientIds;
  private final String message;

  public MessageSentEvent(Long senderId, List<Long> recipientIds, String message) {
    this.senderId = senderId;
    this.recipientIds = recipientIds;
    this.message = message;
  }

  public Long getSenderId() { return senderId; }
  public List<Long> getRecipientIds() { return recipientIds; }
  public String getMessage() { return message; }
}
