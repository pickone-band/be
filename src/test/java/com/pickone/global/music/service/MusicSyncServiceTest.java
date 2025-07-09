package com.pickone.global.music.service;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.music.dto.MusicInfoResponse;
import com.pickone.global.music.dto.MusicSyncResult;
import com.pickone.global.music.entity.Music;
import com.pickone.global.music.mapper.MusicMapper;
import com.pickone.global.music.model.vo.MusicInfo;
import com.pickone.global.music.repository.MusicJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicSyncServiceTest {

  @InjectMocks
  private MusicSyncService musicSyncService;

  @Mock
  private MusicJpaRepository musicRepository;

  @Mock
  private UserJpaRepository userRepository;

  @Mock
  private MusicMapper musicMapper;

  private final Long userId = 1L;
  private final User mockUser = mock(User.class);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void syncUserMusic_shouldSaveOnlyNewTracks() {
    // given
    MusicInfoResponse newTrack = new MusicInfoResponse("Song A", "Artist A", "Album A", "Pop", "trackA");
    MusicInfoResponse existingTrack = new MusicInfoResponse("Song B", "Artist B", "Album B", "Jazz", "trackB");

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

    when(musicRepository.existsByUserIdAndPlatformTrackId(userId, "trackA")).thenReturn(false);
    when(musicRepository.existsByUserIdAndPlatformTrackId(userId, "trackB")).thenReturn(true);

    MusicInfo newTrackInfo = MusicInfo.of("Song A", "Artist A", "Album A", "Pop", "trackA");
    when(musicMapper.toVo(newTrack)).thenReturn(newTrackInfo);

    when(musicRepository.save(any(Music.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    MusicSyncResult result = musicSyncService.syncUserMusic(userId, List.of(newTrack, existingTrack));

    // then
    assertEquals(1, result.newCount());
    assertEquals(1, result.existCount());
    assertEquals(1, result.syncedTracks().size());
    assertEquals("trackA", result.syncedTracks().get(0).platformTrackId());

    verify(musicRepository, times(1)).save(any(Music.class));
  }

  @Test
  void syncUserMusic_userNotFound_shouldThrowException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    MusicInfoResponse track = new MusicInfoResponse("Song", "Artist", "Album", "Genre", "track123");

    assertThrows(BusinessException.class, () ->
        musicSyncService.syncUserMusic(userId, List.of(track))
    );

    verify(musicRepository, never()).save(any());
  }
}
