package com.pickone.global.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "소셜 음악 트랙 응답")
public record SocialMusicTrackResponse(
    @Schema(description = "제목", example = "Bohemian Rhapsody") String title,
    @Schema(description = "아티스트", example = "Queen") String artist,
    @Schema(description = "앨범", example = "A Night at the Opera") String album,
    @Schema(description = "장르", example = "Rock") String genre,
    @Schema(description = "플랫폼 트랙 ID", example = "spotify:track:6Xz0bL23") String platformTrackId,
    @Schema(description = "앨범 커버 이미지 URL") String imageUrl,
    @Schema(description = "플랫폼 트랙 URL") String platformUrl
) {
  public MusicInfoResponse toMusicInfoResponse() {
    return new MusicInfoResponse(title, artist, album, genre, platformTrackId);
  }
}
