package com.pickone.domain.messaging.dto;

import java.time.LocalDateTime;

public record MessageDto(
    String id,
    Long roomId,
    Long senderId,
    Long recipientId,
    String content,
    String status,
    LocalDateTime sentAt,
    LocalDateTime deliveredAt,
    LocalDateTime readAt
) {

}