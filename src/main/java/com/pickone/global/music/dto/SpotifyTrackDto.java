package com.pickone.global.music.dto;

public record SpotifyTrackDto(
    String id, String name, String artist, String album, String genre,
    String imageUrl, String spotifyUrl
) {}