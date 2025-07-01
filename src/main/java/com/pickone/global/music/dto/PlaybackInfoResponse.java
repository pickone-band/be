package com.pickone.global.music.dto;

import java.util.List;

public record PlaybackInfoResponse(
    MusicInfoDto currentTrack,
    List<PlaylistInfoDto> playlists,
    String deviceName,
    boolean isActive
) {}