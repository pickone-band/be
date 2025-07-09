package com.pickone.domain.messaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "채팅방 생성 요청")
public record CreateChatRoomRequest(

    @Schema(description = "채팅방 이름", example = "Study Group")
    String name,

    @Schema(description = "초대할 사용자 ID 목록", example = "[2, 3, 4]")
    List<Long> participantIds
) {}
