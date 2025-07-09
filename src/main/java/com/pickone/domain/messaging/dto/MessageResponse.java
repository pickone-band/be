package com.pickone.domain.messaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "전송된 메시지 응답")
public record MessageResponse(

    @Schema(description = "채팅방 ID", example = "1")
    Long roomId,

    @Schema(description = "보낸 사용자 ID", example = "10")
    Long senderId,

    @Schema(description = "수신자 ID 목록", example = "[11, 12]")
    List<Long> recipientIds,

    @Schema(description = "메시지 내용", example = "안녕하세요!")
    String content,

    @Schema(description = "보낸 시각", example = "2024-07-08T12:00:00")
    LocalDateTime sentAt
) {}