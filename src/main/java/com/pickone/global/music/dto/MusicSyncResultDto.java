package com.pickone.global.music.dto;

import java.util.List;

public record MusicSyncResultDto(
    int newCount,
    int existCount,
    List<SocialMusicTrackDto> syncedTracks
) {}