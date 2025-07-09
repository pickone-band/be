package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentRequest;
import com.pickone.domain.consent.dto.ConsentResponse;
import com.pickone.domain.consent.entity.Consent;
import com.pickone.domain.consent.mapper.ConsentMapper;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import com.pickone.domain.consent.validator.ConsentValidator;
import com.pickone.domain.term.entity.Term;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConsentCommandServiceImplTest {

  @InjectMocks
  private ConsentCommandServiceImpl consentCommandService;

  @Mock
  private UserJpaRepository userRepository;

  @Mock
  private TermJpaRepository termRepository;

  @Mock
  private ConsentJpaRepository consentRepository;

  @Mock
  private ConsentValidator validator;

  @Mock
  private ConsentMapper consentMapper;

  private User user;
  private Term term;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = mock(User.class);
    term = mock(Term.class);
  }

  @Test
  void saveConsent_success() {
    // given
    Long userId = 1L;
    Long termId = 2L;
    ConsentRequest request = new ConsentRequest(termId, true);
    Consent consentEntity = mock(Consent.class);
    Consent savedConsent = mock(Consent.class);
    ConsentResponse response = new ConsentResponse(10L, termId, true, LocalDateTime.now());

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(termRepository.findById(termId)).thenReturn(Optional.of(term));
    doNothing().when(validator).validateSaveConsent(user, term, true);
    when(consentRepository.save(any(Consent.class))).thenReturn(savedConsent);
    when(consentMapper.toDto(savedConsent)).thenReturn(response);

    // when
    ConsentResponse result = consentCommandService.saveConsent(userId, request);

    // then
    assertNotNull(result);
    assertEquals(response, result);
    verify(userRepository).findById(userId);
    verify(termRepository).findById(termId);
    verify(validator).validateSaveConsent(user, term, true);
    verify(consentRepository).save(any(Consent.class));
    verify(consentMapper).toDto(savedConsent);
  }

  @Test
  void saveConsent_fail_userNotFound() {
    // given
    Long userId = 99L;
    ConsentRequest request = new ConsentRequest(1L, true);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex = assertThrows(BusinessException.class,
        () -> consentCommandService.saveConsent(userId, request));

    assertEquals(ErrorCode.USER_INFO_NOT_FOUND, ex.getErrorCode());
    verify(userRepository).findById(userId);
    verifyNoInteractions(termRepository, validator, consentRepository, consentMapper);
  }

  @Test
  void saveConsent_fail_termNotFound() {
    // given
    Long userId = 1L;
    Long termId = 99L;
    ConsentRequest request = new ConsentRequest(termId, true);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(termRepository.findById(termId)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex = assertThrows(BusinessException.class,
        () -> consentCommandService.saveConsent(userId, request));

    assertEquals(ErrorCode.TERM_NOT_FOUND, ex.getErrorCode());
    verify(userRepository).findById(userId);
    verify(termRepository).findById(termId);
    verifyNoInteractions(validator, consentRepository, consentMapper);
  }

  @Test
  void deleteConsent_success() {
    // given
    Long userId = 1L;
    Long termId = 2L;
    Consent consent = mock(Consent.class);
    when(consentRepository.findByUserIdAndTermId(userId, termId)).thenReturn(Optional.of(consent));
    when(consent.getId()).thenReturn(10L);

    // when
    consentCommandService.deleteConsent(userId, termId);

    // then
    verify(consentRepository).findByUserIdAndTermId(userId, termId);
    verify(consentRepository).delete(consent);
  }

  @Test
  void deleteConsent_fail_consentNotFound() {
    // given
    Long userId = 1L;
    Long termId = 99L;
    when(consentRepository.findByUserIdAndTermId(userId, termId)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex = assertThrows(BusinessException.class,
        () -> consentCommandService.deleteConsent(userId, termId));

    assertEquals(ErrorCode.CONSENT_NOT_FOUND, ex.getErrorCode());
    verify(consentRepository).findByUserIdAndTermId(userId, termId);
    verify(consentRepository, never()).delete(any());
  }
}
