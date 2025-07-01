package com.pickone.global.music.dto;

public record SocialMusicTrackDto(
    String title,
    String artist,
    String album,
    String genre,
    String platformTrackId,
    String imageUrl,
    String platformUrl
) {
  // Spotify 변환
  public static SocialMusicTrackDto fromSpotify(SpotifyTrackDto dto) {
    return new SocialMusicTrackDto(
        dto.name(),
        dto.artist(),
        dto.album(),
        dto.genre(),
        dto.id(),
        dto.imageUrl(),
        dto.spotifyUrl()
    );
  }
  // YouTube 변환
  public static SocialMusicTrackDto fromYouTube(YouTubeTrackDto dto) {
    return new SocialMusicTrackDto(
        dto.title(),
        dto.artist(),
        dto.album(),
        null, // YouTube genre 지원 안하면 null
        dto.videoId(),
        dto.imageUrl(),
        dto.youtubeUrl()
    );
  }

  public MusicInfoDto toMusicInfoDto() {
    return new MusicInfoDto(
        title, artist, album, imageUrl, platformUrl, genre, platformTrackId
    );
  }
}
