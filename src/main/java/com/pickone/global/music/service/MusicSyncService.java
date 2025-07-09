package com.pickone.global.music.service;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;

import com.pickone.global.music.dto.MusicInfoResponse;
import com.pickone.global.music.dto.MusicSyncResult;
import com.pickone.global.music.entity.Music;
import com.pickone.global.music.mapper.MusicMapper;
import com.pickone.global.music.model.vo.MusicInfo;
import com.pickone.global.music.repository.MusicJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicSyncService {

  private final MusicJpaRepository musicRepository;
  private final UserJpaRepository userRepository;
  private final MusicMapper musicMapper;

  @Transactional
  public MusicSyncResult syncUserMusic(Long userId, List<MusicInfoResponse> tracks) {
    log.info("[MusicSyncService] 음악 동기화 시작 - userId: {}, 총 트랙 수: {}", userId, tracks.size());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("[MusicSyncService] 유저를 찾을 수 없습니다 - userId: {}", userId);
          throw new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        });

    int newCount = 0, existCount = 0;
    List<MusicInfoResponse> synced = new ArrayList<>();

    for (MusicInfoResponse dto : tracks) {
      String trackId = dto.platformTrackId();
      boolean exists = musicRepository.existsByUserIdAndPlatformTrackId(userId, trackId);

      if (!exists) {
        MusicInfo musicInfo = musicMapper.toVo(dto);
        Music entity = Music.create(musicInfo, user);
        musicRepository.save(entity);
        newCount++;
        synced.add(dto);
        log.info("[MusicSyncService] 신규 트랙 저장 완료 - trackId: {}, userId: {}", trackId, userId);
      } else {
        existCount++;
        log.debug("[MusicSyncService] 이미 저장된 트랙 - trackId: {}, userId: {}", trackId, userId);
      }
    }

    log.info("[MusicSyncService] 동기화 완료 - userId: {}, 신규: {}, 중복: {}", userId, newCount, existCount);
    return new MusicSyncResult(newCount, existCount, synced);
  }
}
