package com.pickone.domain.messaging.dto;

import java.time.LocalDateTime;

public record ChatRoomSummaryDto(
    Long roomId,
    String roomName,
    String lastMessage,
    LocalDateTime lastSentAt
) {

}