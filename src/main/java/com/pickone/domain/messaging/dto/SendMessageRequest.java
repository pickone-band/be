package com.pickone.domain.messaging.dto;

public record SendMessageRequest(
    Long senderId,
    Long recipientId,
    String content
) {

}