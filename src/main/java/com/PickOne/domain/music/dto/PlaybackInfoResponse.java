package com.PickOne.domain.music.dto;

import java.util.List;

public record PlaybackInfoResponse(
        MusicInfo currentTrack,
        List<PlaylistInfo> playlists,
        String deviceName,
        boolean isActiveDevice
) {}