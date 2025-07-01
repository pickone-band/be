package com.pickone.global.music.dto;

public record MusicResponseDto(
    Long id,
    String title,
    String artist,
    String album,
    String genre,
    String platform,
    String platformTrackId
) {}