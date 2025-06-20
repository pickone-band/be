package com.PickOne.domain.music.service;

import com.PickOne.domain.music.dto.MusicInfo;
import com.PickOne.domain.music.dto.PlaylistInfo;

import java.util.List;

public interface MusicService {
    MusicInfo getCurrentlyPlaying(String accessToken);
    List<PlaylistInfo> getPlaylists(String accessToken);
    String getDeviceName(String accessToken);
    boolean isActiveDevice(String accessToken);
}