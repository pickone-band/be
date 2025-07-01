package com.pickone.global.music.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.music.dto.MusicSyncResultDto;
import com.pickone.global.music.dto.SocialMusicTrackDto;
import com.pickone.global.music.model.entity.MusicEntity;
import com.pickone.global.music.model.vo.Music;
import com.pickone.global.music.repository.MusicJpaRepository;
import com.pickone.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicSyncService {
  private final MusicJpaRepository musicRepository;
  private final UserJpaRepository userRepository;

  @Transactional
  public MusicSyncResultDto syncUserMusic(Long userId, List<SocialMusicTrackDto> tracks) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    int newCount = 0, existCount = 0;
    List<SocialMusicTrackDto> synced = new ArrayList<>();

    for (SocialMusicTrackDto track : tracks) {
      boolean exists = musicRepository.existsByUserIdAndPlatformTrackId(userId, track.platformTrackId());
      if (!exists) {
        Music music = Music.from(track); // VO 변환 (정적팩토리 활용)
        MusicEntity entity = MusicEntity.of(music, user);
        musicRepository.save(entity);
        newCount++;
        synced.add(track);
      } else {
        existCount++;
      }
    }
    return new MusicSyncResultDto(newCount, existCount, synced);
  }
}
