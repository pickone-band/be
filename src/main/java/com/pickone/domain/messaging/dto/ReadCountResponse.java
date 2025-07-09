package com.pickone.domain.messaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 읽음 카운트 응답")
public record ReadCountResponse(

    @Schema(description = "메시지 ID", example = "64a123...xyz")
    String messageId,

    @Schema(description = "읽은 사람 수", example = "3")
    long readCount,

    @Schema(description = "총 참여자 수", example = "5")
    long totalCount
) {}