package com.pickone.global.music.dto;

public record MusicSyncRequestDto(
    String platform,
    String accessToken
) {}