package com.pickone.global.music.dto;

public record MusicInfo(
    String title,
    String artist,
    String album,
    String imageUrl,
    String trackUrl
) {

}