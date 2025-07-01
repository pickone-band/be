package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentRequestDto;
import com.pickone.domain.consent.dto.ConsentResponseDto;
import com.pickone.domain.consent.model.entity.ConsentEntity;
import com.pickone.domain.consent.model.policy.ConsentPolicy;
import com.pickone.domain.consent.model.factory.ConsentFactory;
import com.pickone.domain.consent.model.mapper.ConsentMapper;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsentCommandServiceImplTest {

  @Mock private UserJpaRepository userRepository;
  @Mock private TermJpaRepository termRepository;
  @Mock private ConsentJpaRepository consentRepository;
  @Mock private ConsentPolicy consentPolicy;
  @Mock private ConsentFactory consentFactory;

  @InjectMocks
  private ConsentCommandServiceImpl sut; // System Under Test

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("saveConsent")
  class SaveConsent {

    @Test
    @DisplayName("정상적으로 동의 저장")
    void saveConsent_success() {
      // given
      Long userId = 1L, termId = 100L;
      ConsentRequestDto dto = new ConsentRequestDto(termId, true);
      UserEntity user = mock(UserEntity.class);
      TermEntity term = mock(TermEntity.class);
      ConsentEntity entity = mock(ConsentEntity.class);
      ConsentEntity saved = mock(ConsentEntity.class);
      ConsentResponseDto expectedDto = mock(ConsentResponseDto.class);

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(termRepository.findById(termId)).thenReturn(Optional.of(term));
      // 정책 검증은 void
      doNothing().when(consentPolicy).validateSaveConsent(user, term, true);
      when(consentFactory.create(user, term, true)).thenReturn(entity);
      when(consentRepository.save(entity)).thenReturn(saved);

      // ConsentMapper는 static method이므로 mock 불가 → 실제 동작 (구현체가 단순 변환일 것)
      try (MockedStatic<ConsentMapper> mockedMapper = mockStatic(ConsentMapper.class)) {
        mockedMapper.when(() -> ConsentMapper.toDto(saved)).thenReturn(expectedDto);

        // when
        ConsentResponseDto result = sut.saveConsent(userId, dto);

        // then
        assertThat(result).isSameAs(expectedDto);
        verify(userRepository).findById(userId);
        verify(termRepository).findById(termId);
        verify(consentPolicy).validateSaveConsent(user, term, true);
        verify(consentFactory).create(user, term, true);
        verify(consentRepository).save(entity);
      }
    }

    @Test
    @DisplayName("유저가 없으면 예외 발생")
    void saveConsent_userNotFound() {
      Long userId = 1L, termId = 100L;
      ConsentRequestDto dto = new ConsentRequestDto(termId, false);

      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.saveConsent(userId, dto))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("약관이 없으면 예외 발생")
    void saveConsent_termNotFound() {
      Long userId = 1L, termId = 100L;
      ConsentRequestDto dto = new ConsentRequestDto(termId, false);
      UserEntity user = mock(UserEntity.class);

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(termRepository.findById(termId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.saveConsent(userId, dto))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("Term not found");
    }

    @Test
    @DisplayName("정책 위반시 예외 발생")
    void saveConsent_policyViolation() {
      Long userId = 1L, termId = 100L;
      ConsentRequestDto dto = new ConsentRequestDto(termId, true);
      UserEntity user = mock(UserEntity.class);
      TermEntity term = mock(TermEntity.class);

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(termRepository.findById(termId)).thenReturn(Optional.of(term));
      doThrow(new IllegalStateException("Policy violation"))
          .when(consentPolicy).validateSaveConsent(user, term, true);

      assertThatThrownBy(() -> sut.saveConsent(userId, dto))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Policy violation");
    }
  }

  @Nested
  @DisplayName("deleteConsent")
  class DeleteConsent {

    @Test
    @DisplayName("정상적으로 동의 삭제")
    void deleteConsent_success() {
      Long userId = 1L, termId = 100L;
      ConsentEntity entity = mock(ConsentEntity.class);

      when(consentRepository.findByUserIdAndTermId(userId, termId)).thenReturn(Optional.of(entity));

      // when
      sut.deleteConsent(userId, termId);

      // then
      verify(consentRepository).findByUserIdAndTermId(userId, termId);
      verify(consentRepository).delete(entity);
    }

    @Test
    @DisplayName("동의가 없으면 예외 발생")
    void deleteConsent_notFound() {
      Long userId = 1L, termId = 100L;
      when(consentRepository.findByUserIdAndTermId(userId, termId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.deleteConsent(userId, termId))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("Consent not found");
    }
  }
}
