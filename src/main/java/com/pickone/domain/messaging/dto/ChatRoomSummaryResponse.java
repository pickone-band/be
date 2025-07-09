package com.pickone.domain.messaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "채팅방 요약 정보 응답")
public record ChatRoomSummaryResponse(

    @Schema(description = "채팅방 ID", example = "1")
    Long roomId,

    @Schema(description = "채팅방 이름", example = "스터디 그룹")
    String roomName,

    @Schema(description = "최근 메시지 내용", example = "내일 몇시에 만날까요?")
    String lastMessage,

    @Schema(description = "최근 메시지 전송 시간", example = "2024-07-08T12:30:00")
    LocalDateTime lastSentAt
) {}