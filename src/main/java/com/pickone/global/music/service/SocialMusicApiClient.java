package com.pickone.global.music.service;

import com.pickone.global.music.dto.SocialMusicTrackDto;
import com.pickone.global.music.dto.PlaylistInfoDto;
import java.util.List;

public interface SocialMusicApiClient {
  List<PlaylistInfoDto> getPlaylists(String accessToken);
  SocialMusicTrackDto getCurrentlyPlaying(String accessToken);
  List<SocialMusicTrackDto> getTracks(String accessToken);
  String getDeviceName(String accessToken);
  boolean isActiveDevice(String accessToken);
}
