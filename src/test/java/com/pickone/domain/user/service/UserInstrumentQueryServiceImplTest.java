package com.pickone.domain.user.service;

import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import com.pickone.domain.user.repository.UserInstrumentJpaRepository;
import com.pickone.global.common.enums.Instrument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserInstrumentQueryServiceImplTest {

  @Mock private UserInstrumentJpaRepository userInstrumentRepository;
  @InjectMocks private UserInstrumentQueryServiceImpl sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("유저 악기 목록 정상 반환")
  void getUserInstruments_success() {
    Long userId = 10L;
    // 실제 Instrument enum 사용
    UserInstrumentEntity e1 = mock(UserInstrumentEntity.class);
    UserInstrumentEntity e2 = mock(UserInstrumentEntity.class);

    when(e1.getInstrument()).thenReturn(Instrument.KEYBOARD);
    when(e2.getInstrument()).thenReturn(Instrument.ACOUSTIC_GUITAR);

    List<UserInstrumentEntity> entities = Arrays.asList(e1, e2);

    when(userInstrumentRepository.findByUserId(userId)).thenReturn(entities);

    List<Instrument> result = sut.getUserInstruments(userId);

    assertThat(result).containsExactly(Instrument.KEYBOARD, Instrument.ACOUSTIC_GUITAR);
    verify(userInstrumentRepository).findByUserId(userId);
  }

  @Test
  @DisplayName("악기가 없으면 빈 리스트 반환")
  void getUserInstruments_empty() {
    Long userId = 11L;
    when(userInstrumentRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

    List<Instrument> result = sut.getUserInstruments(userId);

    assertThat(result).isEmpty();
    verify(userInstrumentRepository).findByUserId(userId);
  }
}
