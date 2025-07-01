package com.pickone.global.music.dto;

public record YouTubeTrackDto(
    String videoId, String title, String artist, String album,
    String imageUrl, String youtubeUrl
) {}