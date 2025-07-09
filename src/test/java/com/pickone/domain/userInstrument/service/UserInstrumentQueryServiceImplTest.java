package com.pickone.domain.userInstrument.service;

import com.pickone.domain.userInstrument.entity.UserInstrument;
import com.pickone.domain.userInstrument.repository.UserInstrumentJpaRepository;
import com.pickone.global.common.enums.Instrument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserInstrumentQueryServiceImplTest {

  @InjectMocks
  private UserInstrumentQueryServiceImpl userInstrumentQueryService;

  @Mock
  private UserInstrumentJpaRepository userInstrumentRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getUserInstruments_success_multipleInstruments() {
    // given
    Long userId = 1L;

    UserInstrument acousticGuitar = mock(UserInstrument.class);
    UserInstrument drums = mock(UserInstrument.class);
    UserInstrument vocals = mock(UserInstrument.class);

    when(acousticGuitar.getInstrument()).thenReturn(Instrument.ACOUSTIC_GUITAR);
    when(drums.getInstrument()).thenReturn(Instrument.DRUMS);
    when(vocals.getInstrument()).thenReturn(Instrument.VOCALS);

    when(userInstrumentRepository.findByUserId(userId))
        .thenReturn(List.of(acousticGuitar, drums, vocals));

    // when
    List<Instrument> instruments = userInstrumentQueryService.getUserInstruments(userId);

    // then
    assertEquals(3, instruments.size());
    assertTrue(instruments.contains(Instrument.ACOUSTIC_GUITAR));
    assertTrue(instruments.contains(Instrument.DRUMS));
    assertTrue(instruments.contains(Instrument.VOCALS));
    verify(userInstrumentRepository).findByUserId(userId);
  }

  @Test
  void getUserInstruments_success_emptyList() {
    // given
    Long userId = 2L;
    when(userInstrumentRepository.findByUserId(userId)).thenReturn(List.of());

    // when
    List<Instrument> instruments = userInstrumentQueryService.getUserInstruments(userId);

    // then
    assertNotNull(instruments);
    assertTrue(instruments.isEmpty());
    verify(userInstrumentRepository).findByUserId(userId);
  }
}
