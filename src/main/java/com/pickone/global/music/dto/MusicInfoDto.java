package com.pickone.global.music.dto;

public record MusicInfoDto(
    String title,
    String artist,
    String album,
    String imageUrl,
    String platformUrl,
    String genre,
    String platformTrackId
) {}
