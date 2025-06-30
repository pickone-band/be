package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentTermtDto;
import com.pickone.domain.consent.model.entity.ConsentEntity;
import com.pickone.domain.consent.policy.ConsentPolicy;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsentServiceTest {

  @Mock private UserJpaRepository userRepository;
  @Mock private ConsentJpaRepository consentRepository;
  @Mock private TermJpaRepository termRepository;
  @Mock private ConsentPolicy consentPolicy;

  @InjectMocks private ConsentService consentService;

  private UserEntity mockUser;
  private TermEntity mockTerm;
  private ConsentEntity mockConsent;

  @BeforeEach
  void setUp() {
    mockUser = mock(UserEntity.class);
    mockTerm = mock(TermEntity.class);
    mockConsent = mock(ConsentEntity.class);
  }

  @Test
  @DisplayName("단일 동의 저장 성공")
  void saveConsent_success() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
    when(termRepository.findById(2L)).thenReturn(Optional.of(mockTerm));
    when(consentRepository.save(any(ConsentEntity.class))).thenReturn(mockConsent);

    ConsentEntity result = consentService.saveConsent(1L, 2L, true);

    assertThat(result).isNotNull();
    verify(userRepository).findById(1L);
    verify(termRepository).findById(2L);
    verify(consentRepository).save(any(ConsentEntity.class));
  }

  @Test
  @DisplayName("단일 동의 저장 실패 - 유저 없음")
  void saveConsent_userNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    BusinessException ex = assertThrows(
        BusinessException.class,
        () -> consentService.saveConsent(1L, 2L, true)
    );
    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_INFO_NOT_FOUND);
  }

  @Test
  @DisplayName("단일 동의 저장 실패 - 약관 없음")
  void saveConsent_termNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
    when(termRepository.findById(2L)).thenReturn(Optional.empty());

    BusinessException ex = assertThrows(
        BusinessException.class,
        () -> consentService.saveConsent(1L, 2L, true)
    );
    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.TERM_NOT_FOUND);
  }

  @Test
  @DisplayName("복수 동의 저장 성공")
  void saveAll_success() {
    ConsentTermtDto dto1 = new ConsentTermtDto(10L, true);
    ConsentTermtDto dto2 = new ConsentTermtDto(20L, false);

    TermEntity term1 = mock(TermEntity.class);
    TermEntity term2 = mock(TermEntity.class);

    when(termRepository.findById(10L)).thenReturn(Optional.of(term1));
    when(termRepository.findById(20L)).thenReturn(Optional.of(term2));

    // save는 void or 반환값 불필요, just verify
    consentService.saveAll(mockUser, List.of(dto1, dto2));

    verify(termRepository).findById(10L);
    verify(termRepository).findById(20L);
    verify(consentRepository, times(2)).save(any(ConsentEntity.class));
  }

  @Test
  @DisplayName("유저의 전체 동의 내역 조회")
  void getUserConsents_success() {
    when(consentRepository.findByUserId(1L)).thenReturn(List.of(mockConsent));

    List<ConsentEntity> consents = consentService.getUserConsents(1L);

    assertThat(consents).hasSize(1);
    verify(consentRepository).findByUserId(1L);
  }

  @Test
  @DisplayName("동의여부 정책 검사 위임")
  void hasConsented_delegatesToPolicy() {
    when(consentPolicy.hasConsented(1L, 2L)).thenReturn(true);

    boolean result = consentService.hasConsented(1L, 2L);

    assertThat(result).isTrue();
    verify(consentPolicy).hasConsented(1L, 2L);
  }

  @Test
  @DisplayName("동의 삭제 성공")
  void deleteConsent_success() {
    when(consentRepository.findByUserIdAndTermId(1L, 2L)).thenReturn(Optional.of(mockConsent));

    consentService.deleteConsent(1L, 2L);

    verify(consentRepository).findByUserIdAndTermId(1L, 2L);
    verify(consentRepository).delete(mockConsent);
  }

  @Test
  @DisplayName("동의 삭제 실패 - 엔티티 없음")
  void deleteConsent_notFound() {
    when(consentRepository.findByUserIdAndTermId(1L, 2L)).thenReturn(Optional.empty());

    BusinessException ex = assertThrows(
        BusinessException.class,
        () -> consentService.deleteConsent(1L, 2L)
    );
    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CONSENT_NOT_FOUND);
  }
}