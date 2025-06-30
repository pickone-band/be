package com.pickone.domain.user.service;

import static org.mockito.ArgumentMatchers.anyList;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


import com.pickone.domain.user.dto.InstrumentInfoDto;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import com.pickone.domain.user.repository.UserInstrumentJpaRepository;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Mbti;
import com.pickone.global.common.enums.Proficiency;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserInstrumentServiceTest {

  @Mock
  private UserInstrumentJpaRepository instrumentRepository;
  @InjectMocks
  private UserInstrumentService instrumentService;

  private UserEntity user;

  @BeforeEach
  void setup() {
    user = UserEntity.of("email", "pwd", "nick", Gender.FEMALE,
        LocalDate.of(1995, 5, 15), Mbti.ENFP, List.of(Genre.JAZZ));
  }

  @Test
  void saveAll_withNullOrEmptyList_shouldDoNothing() {
    instrumentService.saveAll(user, null);
    instrumentService.saveAll(user, List.of());
    verify(instrumentRepository, never()).saveAll(anyList());
  }

  @Test
  void saveAll_success() {
    List<InstrumentInfoDto> dtos = List.of(
        new InstrumentInfoDto(Instrument.ACOUSTIC_GUITAR, Proficiency.INTERMEDIATE),
        new InstrumentInfoDto(Instrument.TRUMPET, Proficiency.BEGINNER)
    );

    instrumentService.saveAll(user, dtos);

    verify(instrumentRepository).saveAll(argThat((List<UserInstrumentEntity> list) ->
        list.size() == 2 &&
            list.stream().allMatch(e -> e.getUser() == user)
    ));
  }
}
