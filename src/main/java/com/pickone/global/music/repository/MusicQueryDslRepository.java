package com.pickone.global.music.repository;

public interface MusicQueryDslRepository {
  boolean existsByUserIdAndPlatformTrackId(Long userId, String platformTrackId);

}
