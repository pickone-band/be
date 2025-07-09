package com.pickone.domain.messaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "채팅방 상세 정보 응답")
public record ChatRoomDetailResponse(

    @Schema(description = "채팅방 ID", example = "1")
    Long roomId,

    @Schema(description = "채팅방 이름", example = "스터디")
    String name,

    @Schema(description = "참여자 닉네임 목록", example = "[\"철수\", \"영희\"]")
    List<String> participantNicknames
) {}