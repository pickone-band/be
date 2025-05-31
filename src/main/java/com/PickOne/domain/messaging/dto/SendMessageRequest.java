package com.PickOne.domain.messaging.dto;

public record SendMessageRequest(
        Long senderId,
        Long recipientId,
        String content
) {}