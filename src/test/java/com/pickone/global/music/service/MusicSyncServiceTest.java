package com.pickone.global.music.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.music.dto.MusicSyncResultDto;
import com.pickone.global.music.dto.SocialMusicTrackDto;
import com.pickone.global.music.model.entity.MusicEntity;
import com.pickone.global.music.model.vo.Music;
import com.pickone.global.music.repository.MusicJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicSyncServiceTest {

  @Mock private MusicJpaRepository musicRepository;
  @Mock private UserJpaRepository userRepository;
  @InjectMocks private MusicSyncService sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("새 트랙만 저장, 이미 존재 트랙은 무시")
  void syncUserMusic_saveOnlyNew() {
    Long userId = 1L;
    UserEntity user = mock(UserEntity.class);
    SocialMusicTrackDto track1 = mock(SocialMusicTrackDto.class);
    SocialMusicTrackDto track2 = mock(SocialMusicTrackDto.class);

    List<SocialMusicTrackDto> tracks = Arrays.asList(track1, track2);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(track1.platformTrackId()).thenReturn("track1");
    when(track2.platformTrackId()).thenReturn("track2");

    // track1은 존재하지 않음, track2는 이미 있음
    when(musicRepository.existsByUserIdAndPlatformTrackId(userId, "track1")).thenReturn(false);
    when(musicRepository.existsByUserIdAndPlatformTrackId(userId, "track2")).thenReturn(true);

    // VO 및 Entity static factory
    Music music = mock(Music.class);
    MusicEntity entity = mock(MusicEntity.class);
    try (MockedStatic<Music> musicStatic = mockStatic(Music.class);
        MockedStatic<MusicEntity> entityStatic = mockStatic(MusicEntity.class)) {

      musicStatic.when(() -> Music.from(track1)).thenReturn(music);
      entityStatic.when(() -> MusicEntity.of(music, user)).thenReturn(entity);
      when(musicRepository.save(entity)).thenReturn(entity);

      MusicSyncResultDto result = sut.syncUserMusic(userId, tracks);

      assertThat(result.newCount()).isEqualTo(1);
      assertThat(result.existCount()).isEqualTo(1);
      assertThat(result.syncedTracks()).containsExactly(track1);
      verify(musicRepository).save(entity);
    }
  }

  @Test
  @DisplayName("모든 트랙 이미 존재하면 저장하지 않음")
  void syncUserMusic_allExist() {
    Long userId = 2L;
    UserEntity user = mock(UserEntity.class);
    SocialMusicTrackDto track1 = mock(SocialMusicTrackDto.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(track1.platformTrackId()).thenReturn("id");

    when(musicRepository.existsByUserIdAndPlatformTrackId(userId, "id")).thenReturn(true);

    MusicSyncResultDto result = sut.syncUserMusic(userId, List.of(track1));

    assertThat(result.newCount()).isZero();
    assertThat(result.existCount()).isEqualTo(1);
    assertThat(result.syncedTracks()).isEmpty();
    verify(musicRepository, never()).save(any());
  }

  @Test
  @DisplayName("유저 없으면 예외 발생")
  void syncUserMusic_userNotFound() {
    Long userId = 999L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> sut.syncUserMusic(userId, List.of()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("User not found");
  }
}
