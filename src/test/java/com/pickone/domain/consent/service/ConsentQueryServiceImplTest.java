package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentResponse;
import com.pickone.domain.consent.entity.Consent;
import com.pickone.domain.consent.mapper.ConsentMapper;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsentQueryServiceImplTest {

  @InjectMocks
  private ConsentQueryServiceImpl consentQueryService;

  @Mock
  private ConsentJpaRepository consentRepository;

  @Mock
  private ConsentMapper consentMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getUserConsents_success() {
    // given
    Long userId = 1L;
    Consent consent1 = mock(Consent.class);
    Consent consent2 = mock(Consent.class);

    ConsentResponse response1 = new ConsentResponse(10L, 100L, true, LocalDateTime.now());
    ConsentResponse response2 = new ConsentResponse(11L, 101L, false, LocalDateTime.now());

    when(consentRepository.findByUserId(userId)).thenReturn(List.of(consent1, consent2));
    when(consentMapper.toDto(consent1)).thenReturn(response1);
    when(consentMapper.toDto(consent2)).thenReturn(response2);

    // when
    List<ConsentResponse> results = consentQueryService.getUserConsents(userId);

    // then
    assertEquals(2, results.size());
    assertEquals(response1, results.get(0));
    assertEquals(response2, results.get(1));

    verify(consentRepository).findByUserId(userId);
    verify(consentMapper, times(2)).toDto(any(Consent.class));
  }

  @Test
  void getUserConsents_emptyList() {
    // given
    Long userId = 1L;
    when(consentRepository.findByUserId(userId)).thenReturn(List.of());

    // when
    List<ConsentResponse> results = consentQueryService.getUserConsents(userId);

    // then
    assertTrue(results.isEmpty());
    verify(consentRepository).findByUserId(userId);
    verifyNoInteractions(consentMapper);
  }

  @Test
  void hasConsented_true() {
    // given
    Long userId = 1L;
    Long termId = 100L;
    when(consentRepository.existsByUserIdAndTermIdAndConsentedTrue(userId, termId)).thenReturn(true);

    // when
    boolean result = consentQueryService.hasConsented(userId, termId);

    // then
    assertTrue(result);
    verify(consentRepository).existsByUserIdAndTermIdAndConsentedTrue(userId, termId);
  }

  @Test
  void hasConsented_false() {
    // given
    Long userId = 1L;
    Long termId = 100L;
    when(consentRepository.existsByUserIdAndTermIdAndConsentedTrue(userId, termId)).thenReturn(false);

    // when
    boolean result = consentQueryService.hasConsented(userId, termId);

    // then
    assertFalse(result);
    verify(consentRepository).existsByUserIdAndTermIdAndConsentedTrue(userId, termId);
  }
}
