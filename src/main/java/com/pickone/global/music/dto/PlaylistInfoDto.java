package com.pickone.global.music.dto;

// PlaylistInfo.java
public record PlaylistInfoDto(
    String id,
    String title,
    String description,
    String imageUrl,
    int trackCount      // 유튜브의 경우 0이면 track count 미지원
) {}
