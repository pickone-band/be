package com.pickone.domain.messaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 전송 요청")
public record SendMessageRequest(

    @Schema(description = "채팅방 ID", example = "1")
    Long roomId,

    @Schema(description = "메시지 내용", example = "안녕하세요")
    String content
) {}