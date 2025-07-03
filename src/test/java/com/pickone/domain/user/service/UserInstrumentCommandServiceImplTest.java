package com.pickone.domain.user.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import com.pickone.domain.user.repository.UserInstrumentJpaRepository;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
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

      UserInstrumentEntity entity = UserInstrumentEntity.of(user, instrument, Proficiency.NEVER_PLAYED);
      when(userInstrumentRepository.save(any(UserInstrumentEntity.class))).thenReturn(entity);

      sut.addInstrument(userId, instrument);

      verify(userRepository).findById(userId);
      verify(userInstrumentRepository).save(any(UserInstrumentEntity.class));
      verify(user).getInstruments();

      // 리스트에 실제로 추가됐는지 확인
      assertThat(userInstruments)
          .anyMatch(ui -> ui.getInstrument() == instrument && ui.getProficiency() == Proficiency.NEVER_PLAYED);
    }

    @Test
    @DisplayName("유저가 없으면 예외 발생")
    void addInstrument_userNotFound() {
      Long userId = 1L;
      Instrument instrument = Instrument.PERCUSSION;
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.addInstrument(userId, instrument))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
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
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
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

      // Save 호출시 UserInstrumentEntity.of를 직접 호출하여 실제 객체 생성
      for (Instrument i : instruments) {
        UserInstrumentEntity entity = UserInstrumentEntity.of(user, i, Proficiency.NEVER_PLAYED);
        when(userInstrumentRepository.save(any(UserInstrumentEntity.class))).thenReturn(entity);
      }

      sut.setInstruments(userId, instruments);

      verify(userRepository).findById(userId);
      verify(userInstrumentRepository).deleteByUserId(userId);
      verify(userInstrumentRepository, times(instruments.size())).save(any(UserInstrumentEntity.class));
    }

    @Test
    @DisplayName("유저가 없으면 예외 발생")
    void setInstruments_userNotFound() {
      Long userId = 4L;
      List<Instrument> instruments = List.of(Instrument.FLUTE);
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.setInstruments(userId, instruments))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
    }
  }
}
