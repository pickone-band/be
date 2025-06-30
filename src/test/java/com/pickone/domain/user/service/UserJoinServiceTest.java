package com.pickone.domain.user.service;

import com.pickone.domain.consent.dto.ConsentTermtDto;
import com.pickone.domain.consent.service.ConsentService;
import com.pickone.domain.user.dto.InstrumentInfoDto;
import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserJoinServiceTest {

  private UserCreator userCreator;
  private UserInstrumentService userInstrumentService;
  private ConsentService consentService;
  private UserJoinService userJoinService;

  @BeforeEach
  void setUp() {
    userCreator = mock(UserCreator.class);
    userInstrumentService = mock(UserInstrumentService.class);
    consentService = mock(ConsentService.class);
    userJoinService = new UserJoinService(userCreator, userInstrumentService, consentService);
  }

  @Test
  void join_callsAllServices() {
    // Given
    SignupRequestDto requestDto = mock(SignupRequestDto.class);
    UserEntity user = mock(UserEntity.class);

    List<InstrumentInfoDto> instruments = List.of(
        new InstrumentInfoDto(Instrument.ACOUSTIC_GUITAR, Proficiency.INTERMEDIATE),
        new InstrumentInfoDto(Instrument.PERCUSSION, Proficiency.BEGINNER)
    );

    ConsentTermtDto agreement1 = new ConsentTermtDto(1L, true);
    ConsentTermtDto agreement2 = new ConsentTermtDto(2L, false);
    List<ConsentTermtDto> agreements = List.of(agreement1, agreement2);

    when(userCreator.createUser(requestDto)).thenReturn(user);
    when(requestDto.instruments()).thenReturn(instruments);
    when(requestDto.agreements()).thenReturn(agreements);

    // When
    userJoinService.join(requestDto);

    // Then
    verify(userCreator).createUser(requestDto);
    verify(userInstrumentService).saveAll(eq(user), eq(instruments));
    verify(consentService).saveAll(eq(user), eq(agreements));
    verifyNoMoreInteractions(userInstrumentService);
  }
}
