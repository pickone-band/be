package com.pickone.global.music.dto;

public record MusicRequestDto(
    String title,
    String artist,
    String album,
    String genre
) {}