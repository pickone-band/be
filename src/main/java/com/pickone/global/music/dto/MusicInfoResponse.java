package com.pickone.global.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "음악 정보 응답")
public record MusicInfoResponse(
    @Schema(description = "곡 제목", example = "Bohemian Rhapsody") String title,
    @Schema(description = "아티스트", example = "Queen") String artist,
    @Schema(description = "앨범", example = "A Night at the Opera") String album,
    @Schema(description = "장르", example = "Rock") String genre,
    @Schema(description = "플랫폼 트랙 ID", example = "6Xz0bL23Q8mKdaZr5qD1oL") String platformTrackId
) {}
