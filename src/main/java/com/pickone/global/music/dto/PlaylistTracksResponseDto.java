package com.pickone.global.music.dto;

import java.util.List;

public record PlaylistTracksResponseDto(
    String playlistId,
    List<MusicInfoDto> tracks
) {}