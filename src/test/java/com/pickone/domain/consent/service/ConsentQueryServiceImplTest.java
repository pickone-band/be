package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentResponseDto;
import com.pickone.domain.consent.model.entity.ConsentEntity;
import com.pickone.domain.consent.model.mapper.ConsentMapper;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsentQueryServiceImplTest {

  @Mock private ConsentJpaRepository consentRepository;
  @InjectMocks private ConsentQueryServiceImpl sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("getUserConsents: 동의 내역 리스트 정상 반환")
  void getUserConsents_success() {
    // given
    Long userId = 1L;
    ConsentEntity entity1 = mock(ConsentEntity.class);
    ConsentEntity entity2 = mock(ConsentEntity.class);
    List<ConsentEntity> entities = Arrays.asList(entity1, entity2);

    ConsentResponseDto dto1 = mock(ConsentResponseDto.class);
    ConsentResponseDto dto2 = mock(ConsentResponseDto.class);

    when(consentRepository.findByUserId(userId)).thenReturn(entities);

    // static method mocking
    try (MockedStatic<ConsentMapper> mockedMapper = mockStatic(ConsentMapper.class)) {
      mockedMapper.when(() -> ConsentMapper.toDto(entity1)).thenReturn(dto1);
      mockedMapper.when(() -> ConsentMapper.toDto(entity2)).thenReturn(dto2);

      // when
      List<ConsentResponseDto> result = sut.getUserConsents(userId);

      // then
      assertThat(result).containsExactly(dto1, dto2);
      verify(consentRepository).findByUserId(userId);
    }
  }

  @Test
  @DisplayName("getUserConsents: 동의 내역이 없으면 빈 리스트 반환")
  void getUserConsents_empty() {
    Long userId = 2L;
    when(consentRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

    List<ConsentResponseDto> result = sut.getUserConsents(userId);

    assertThat(result).isEmpty();
    verify(consentRepository).findByUserId(userId);
  }

  @Test
  @DisplayName("hasConsented: 동의 내역 존재시 true 반환")
  void hasConsented_true() {
    Long userId = 1L, termId = 100L;
    when(consentRepository.existsByUserIdAndTermIdAndConsentedTrue(userId, termId))
        .thenReturn(true);

    boolean result = sut.hasConsented(userId, termId);

    assertThat(result).isTrue();
    verify(consentRepository).existsByUserIdAndTermIdAndConsentedTrue(userId, termId);
  }

  @Test
  @DisplayName("hasConsented: 동의 내역 미존재시 false 반환")
  void hasConsented_false() {
    Long userId = 1L, termId = 100L;
    when(consentRepository.existsByUserIdAndTermIdAndConsentedTrue(userId, termId))
        .thenReturn(false);

    boolean result = sut.hasConsented(userId, termId);

    assertThat(result).isFalse();
    verify(consentRepository).existsByUserIdAndTermIdAndConsentedTrue(userId, termId);
  }
}
