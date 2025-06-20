package com.PickOne.domain.music.dto;

public record PlaylistInfo(
        String id,
        String name,
        String description,
        String imageUrl,
        int trackCount
) {}