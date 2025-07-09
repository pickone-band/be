package com.pickone.domain.userInstrument.service;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.userInstrument.entity.UserInstrument;
import com.pickone.domain.userInstrument.repository.UserInstrumentJpaRepository;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserInstrumentCommandServiceImplTest {

  @InjectMocks
  private UserInstrumentCommandServiceImpl service;

  @Mock
  private UserJpaRepository userRepository;

  @Mock
  private UserInstrumentJpaRepository userInstrumentRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addInstrument_success() {
    Long userId = 1L;
    Instrument instrument = Instrument.ACOUSTIC_GUITAR;
    User user = mock(User.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    service.addInstrument(userId, instrument);

    verify(userRepository).findById(userId);
    verify(userInstrumentRepository).save(any(UserInstrument.class));
    verify(user).getInstruments(); // 연관 관계에 추가되었는지 확인
  }

  @Test
  void addInstrument_userNotFound() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> service.addInstrument(userId, Instrument.SAXOPHONE));
  }

  @Test
  void removeInstrument_success() {
    Long userId = 1L;
    Instrument instrument = Instrument.VIOLIN;
    UserInstrument entity = mock(UserInstrument.class);

    when(userInstrumentRepository.findByUserIdAndInstrument(userId, instrument))
        .thenReturn(Optional.of(entity));

    service.removeInstrument(userId, instrument);

    verify(userInstrumentRepository).delete(entity);
  }

  @Test
  void removeInstrument_notFound() {
    Long userId = 1L;
    Instrument instrument = Instrument.FLUTE;

    when(userInstrumentRepository.findByUserIdAndInstrument(userId, instrument))
        .thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> service.removeInstrument(userId, instrument));
  }

  @Test
  void setInstruments_success() {
    Long userId = 1L;
    User user = mock(User.class);
    List<Instrument> instruments = List.of(Instrument.KEYBOARD, Instrument.DRUMS);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    service.setInstruments(userId, instruments);

    verify(userInstrumentRepository).deleteByUserId(userId);
    verify(userInstrumentRepository, times(instruments.size())).save(any(UserInstrument.class));
  }

  @Test
  void setInstruments_emptyList() {
    Long userId = 1L;
    User user = mock(User.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    service.setInstruments(userId, List.of());

    verify(userInstrumentRepository).deleteByUserId(userId);
    verify(userInstrumentRepository, never()).save(any());
  }

  @Test
  void setInstruments_userNotFound() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> service.setInstruments(userId, List.of(Instrument.PERCUSSION)));
  }
}
