package com.pickone.global.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "재생 상태 전체 응답")
public record PlaybackInfoResponse(
    @Schema(description = "현재 재생 중인 음악 정보") MusicInfoResponse currentTrack,
    @Schema(description = "사용자의 플레이리스트 목록") List<PlaylistInfoResponse> playlists,
    @Schema(description = "현재 재생 중인 디바이스 이름", example = "iPhone 13 Pro") String deviceName,
    @Schema(description = "활성 디바이스 여부", example = "true") boolean isActiveDevice
) {}