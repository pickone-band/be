package com.pickone.global.music.service;

import com.pickone.global.music.dto.MusicInfo;
import com.pickone.global.music.dto.PlaylistInfo;

import java.util.List;

public interface MusicService {

  MusicInfo getCurrentlyPlaying(String accessToken);

  List<PlaylistInfo> getPlaylists(String accessToken);

  String getDeviceName(String accessToken);

  boolean isActiveDevice(String accessToken);
}