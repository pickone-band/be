package com.pickone.domain.user.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import com.pickone.domain.user.repository.UserInstrumentJpaRepository;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Instrument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserInstrumentCommandServiceImplTest {

  @Mock private UserJpaRepository userRepository;
  @Mock private UserInstrumentJpaRepository userInstrumentRepository;
  @InjectMocks private UserInstrumentCommandServiceImpl sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("addInstrument")
  class AddInstrument {
    @Test
    @DisplayName("정상적으로 악기 추가 (ELECTRIC_GUITAR)")
    void addInstrument_success() {
      Long userId = 1L;
      Instrument instrument = Instrument.ELECTRIC_GUITAR;

      UserEntity user = mock(UserEntity.class);
      List<UserInstrumentEntity> userInstruments = new ArrayList<>();
      when(user.getInstruments()).thenReturn(userInstruments);
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));

      UserInstrumentEntity entity = mock(UserInstrumentEntity.class);
      try (MockedStatic<UserInstrumentEntity> builderStatic = mockStatic(UserInstrumentEntity.class, Mockito.CALLS_REAL_METHODS)) {
        UserInstrumentEntity.UserInstrumentEntityBuilder builder = mock(UserInstrumentEntity.UserInstrumentEntityBuilder.class, RETURNS_SELF);
        builderStatic.when(UserInstrumentEntity::builder).thenReturn(builder);
        when(builder.user(user)).thenReturn(builder);
        when(builder.instrument(instrument)).thenReturn(builder);
        when(builder.build()).thenReturn(entity);

        when(userInstrumentRepository.save(entity)).thenReturn(entity);

        sut.addInstrument(userId, instrument);

        verify(userRepository).findById(userId);
        verify(userInstrumentRepository).save(entity);
        verify(user).getInstruments();
        assertThat(userInstruments).contains(entity);
      }
    }

    @Test
    @DisplayName("유저가 없으면 예외 발생")
    void addInstrument_userNotFound() {
      Long userId = 1L;
      Instrument instrument = Instrument.PERCUSSION;
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.addInstrument(userId, instrument))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("User not found");
    }
  }

  @Nested
  @DisplayName("removeInstrument")
  class RemoveInstrument {
    @Test
    @DisplayName("정상적으로 악기 제거 (VIOLIN)")
    void removeInstrument_success() {
      Long userId = 2L;
      Instrument instrument = Instrument.VIOLIN;
      UserInstrumentEntity entity = mock(UserInstrumentEntity.class);
      when(userInstrumentRepository.findByUserIdAndInstrument(userId, instrument))
          .thenReturn(Optional.of(entity));
      doNothing().when(userInstrumentRepository).delete(entity);

      sut.removeInstrument(userId, instrument);

      verify(userInstrumentRepository).findByUserIdAndInstrument(userId, instrument);
      verify(userInstrumentRepository).delete(entity);
    }

    @Test
    @DisplayName("악기 매핑이 없으면 예외")
    void removeInstrument_notFound() {
      Long userId = 2L;
      Instrument instrument = Instrument.CELLO;
      when(userInstrumentRepository.findByUserIdAndInstrument(userId, instrument))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.removeInstrument(userId, instrument))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("Not found");
    }
  }

  @Nested
  @DisplayName("setInstruments")
  class SetInstruments {
    @Test
    @DisplayName("악기 전체 세팅(여러 종류, 기존 삭제 후 추가)")
    void setInstruments_success() {
      Long userId = 3L;
      List<Instrument> instruments = Arrays.asList(
          Instrument.ACOUSTIC_GUITAR,
          Instrument.DRUMS,
          Instrument.TRUMPET
      );

      UserEntity user = mock(UserEntity.class);
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));

      doNothing().when(userInstrumentRepository).deleteByUserId(userId);

      // 모든 builder static mocking은 한번만 해도 충분, 혹은 각 악기에 대해 반복적으로 할 수도 있음
      for (Instrument i : instruments) {
        UserInstrumentEntity entity = mock(UserInstrumentEntity.class);
        try (MockedStatic<UserInstrumentEntity> builderStatic = mockStatic(UserInstrumentEntity.class, Mockito.CALLS_REAL_METHODS)) {
          UserInstrumentEntity.UserInstrumentEntityBuilder builder = mock(UserInstrumentEntity.UserInstrumentEntityBuilder.class, RETURNS_SELF);
          builderStatic.when(UserInstrumentEntity::builder).thenReturn(builder);
          when(builder.user(user)).thenReturn(builder);
          when(builder.instrument(i)).thenReturn(builder);
          when(builder.build()).thenReturn(entity);
          when(userInstrumentRepository.save(entity)).thenReturn(entity);
        }
      }

      sut.setInstruments(userId, instruments);

      verify(userRepository).findById(userId);
      verify(userInstrumentRepository).deleteByUserId(userId);
      // 악기 수만큼 save가 호출되어야 함
      verify(userInstrumentRepository, times(instruments.size())).save(any(UserInstrumentEntity.class));
    }


    @Test
    @DisplayName("유저가 없으면 예외 발생")
    void setInstruments_userNotFound() {
      Long userId = 4L;
      List<Instrument> instruments = List.of(Instrument.FLUTE);
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.setInstruments(userId, instruments))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("User not found");
    }
  }
}
