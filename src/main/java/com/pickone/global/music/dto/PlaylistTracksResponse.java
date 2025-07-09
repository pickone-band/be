package com.pickone.global.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "플레이리스트 트랙 응답")
public record PlaylistTracksResponse(
    @Schema(description = "플레이리스트 ID", example = "37i9dQZF1DWXRqgorJj26U") String playlistId,
    @Schema(description = "트랙 목록") List<MusicInfoResponse> tracks
) {}
