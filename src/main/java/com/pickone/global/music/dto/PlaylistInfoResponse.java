package com.pickone.global.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "플레이리스트 정보")
public record PlaylistInfoResponse(
    @Schema(description = "플레이리스트 ID", example = "37i9dQZF1DXcBWIGoYBM5M") String id,
    @Schema(description = "플레이리스트 제목", example = "Today's Top Hits") String title,
    @Schema(description = "설명") String description,
    @Schema(description = "썸네일 이미지 URL") String imageUrl,
    @Schema(description = "트랙 수", example = "50") int trackCount
) {}
