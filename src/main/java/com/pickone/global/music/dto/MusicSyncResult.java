package com.pickone.global.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "소셜 음악 동기화 결과")
public record MusicSyncResult(
    @Schema(description = "새로 추가된 곡 수", example = "3") int newCount,
    @Schema(description = "이미 존재했던 곡 수", example = "17") int existCount,
    @Schema(description = "실제로 동기화된 곡 목록") List<MusicInfoResponse> syncedTracks
) {}
