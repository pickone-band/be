package com.pickone.global.music.service;

import com.pickone.global.music.dto.PlaylistInfoResponse;
import com.pickone.global.music.dto.SocialMusicTrackResponse;
import java.util.List;

public interface SocialMusicApiClient {
  List<PlaylistInfoResponse> getPlaylists(String accessToken);
  SocialMusicTrackResponse getCurrentlyPlaying(String accessToken);
  List<SocialMusicTrackResponse> getTracks(String accessToken);
  String getDeviceName(String accessToken);
  boolean isActiveDevice(String accessToken);
}