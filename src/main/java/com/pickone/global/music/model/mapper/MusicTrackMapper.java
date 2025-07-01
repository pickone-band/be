package com.pickone.global.music.model.mapper;

import com.pickone.global.music.dto.SocialMusicTrackDto;
import com.pickone.global.music.dto.SpotifyTrackDto;
import com.pickone.global.music.dto.YouTubeTrackDto;

public class MusicTrackMapper {
  public static SocialMusicTrackDto toSocialDto(SpotifyTrackDto dto) {
    return SocialMusicTrackDto.fromSpotify(dto);
  }
  public static SocialMusicTrackDto toSocialDto(YouTubeTrackDto dto) {
    return SocialMusicTrackDto.fromYouTube(dto);
  }
  // 필요시 Apple, Melon 등 확장
}
