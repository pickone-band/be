package com.pickone.global.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DB에 저장된 음악 정보")
public record MusicResponse(
    @Schema(description = "음악 ID", example = "1") Long id,
    @Schema(description = "음악 정보") MusicInfoResponse musicInfo,
    @Schema(description = "사용자 ID", example = "42") Long userId
) {}
