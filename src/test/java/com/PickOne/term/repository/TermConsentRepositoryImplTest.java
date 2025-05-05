package com.PickOne.term.repository;

import com.PickOne.term.model.domain.TermConsent;
import com.PickOne.term.model.entity.TermsConsentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TermConsentRepositoryImpl 클래스에 대한 단위 테스트
 * Lenient 설정을 통해 불필요한 모킹 경고를 방지
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TermConsentRepositoryImplTest {

    @Mock
    private JpaTermConsentRepository jpaRepository;

    @InjectMocks
    private TermConsentRepositoryImpl repository;

    private final Long userId = 1L;
    private final Long termsId = 2L;
    private final LocalDateTime consentDate = LocalDateTime.now();
    /**
     * 약관 동의 저장 테스트 - 신규 동의 케이스
     */
    @Test
    @DisplayName("약관 동의 저장 테스트 - 신규 동의 케이스")
    void save_NewConsent() {
        // given
        TermConsent consent = mock(TermConsent.class);
        when(consent.getUserId()).thenReturn(userId);
        when(consent.getTermsId()).thenReturn(termsId);
        when(consent.isConsented()).thenReturn(true);
        when(consent.getConsentDate()).thenReturn(consentDate);

        when(jpaRepository.findByUserIdAndTermsId(userId, termsId)).thenReturn(Optional.empty());

        TermsConsentEntity savedEntity = mock(TermsConsentEntity.class);
        TermConsent savedDomain = mock(TermConsent.class);
        when(savedEntity.toDomain()).thenReturn(savedDomain);
        when(jpaRepository.save(any(TermsConsentEntity.class))).thenReturn(savedEntity);

        // when
        TermConsent result = repository.save(consent);

        // then
        verify(jpaRepository).findByUserIdAndTermsId(userId, termsId);
        verify(jpaRepository).save(any(TermsConsentEntity.class));
        assertThat(result).isEqualTo(savedDomain);
    }

    /**
     * 약관 동의 저장 테스트 - 기존 동의 업데이트 케이스
     */
    @Test
    @DisplayName("약관 동의 저장 테스트 - 기존 동의 업데이트 케이스")
    void save_UpdateExistingConsent() {
        // given
        TermConsent consent = mock(TermConsent.class);
        when(consent.getUserId()).thenReturn(userId);
        when(consent.getTermsId()).thenReturn(termsId);
        when(consent.isConsented()).thenReturn(true);
        when(consent.getConsentDate()).thenReturn(consentDate);

        TermsConsentEntity existingEntity = mock(TermsConsentEntity.class);
        when(jpaRepository.findByUserIdAndTermsId(userId, termsId)).thenReturn(Optional.of(existingEntity));

        TermsConsentEntity savedEntity = mock(TermsConsentEntity.class);
        TermConsent savedDomain = mock(TermConsent.class);
        when(savedEntity.toDomain()).thenReturn(savedDomain);
        when(jpaRepository.save(existingEntity)).thenReturn(savedEntity);

        // when
        TermConsent result = repository.save(consent);

        // then
        verify(jpaRepository).findByUserIdAndTermsId(userId, termsId);
        verify(existingEntity).updateConsent(true, consentDate);
        verify(jpaRepository).save(existingEntity);
        assertThat(result).isEqualTo(savedDomain);
    }

    /**
     * 사용자 ID와 약관 ID로 동의 정보 조회 테스트
     */
    @Test
    @DisplayName("사용자 ID와 약관 ID로 동의 정보 조회 테스트")
    void findByUserIdAndTermsId_Found() {
        // given
        TermsConsentEntity entity = mock(TermsConsentEntity.class);
        TermConsent domain = mock(TermConsent.class);
        when(entity.toDomain()).thenReturn(domain);

        when(jpaRepository.findByUserIdAndTermsId(userId, termsId)).thenReturn(Optional.of(entity));

        // when
        Optional<TermConsent> result = repository.findByUserIdAndTermsId(userId, termsId);

        // then
        verify(jpaRepository).findByUserIdAndTermsId(userId, termsId);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);
    }

    /**
     * 사용자 ID로 모든 동의 정보 조회 테스트
     */
    @Test
    @DisplayName("사용자 ID로 모든 동의 정보 조회 테스트")
    void findAllByUserId_Success() {
        // given
        TermsConsentEntity entity1 = mock(TermsConsentEntity.class);
        TermsConsentEntity entity2 = mock(TermsConsentEntity.class);
        TermConsent domain1 = mock(TermConsent.class);
        TermConsent domain2 = mock(TermConsent.class);

        when(entity1.toDomain()).thenReturn(domain1);
        when(entity2.toDomain()).thenReturn(domain2);

        List<TermsConsentEntity> entities = Arrays.asList(entity1, entity2);
        when(jpaRepository.findAllByUserId(userId)).thenReturn(entities);

        // when
        List<TermConsent> results = repository.findAllByUserId(userId);

        // then
        verify(jpaRepository).findAllByUserId(userId);
        assertThat(results).hasSize(2);
        assertThat(results).containsExactly(domain1, domain2);
    }

    /**
     * 사용자의 약관 동의 여부 조회 테스트
     */
    @Test
    @DisplayName("사용자의 약관 동의 여부 조회 테스트 - 동의한 경우")
    void hasUserConsented_True() {
        // given
        when(jpaRepository.hasUserConsented(userId, termsId)).thenReturn(true);

        // when
        boolean result = repository.hasUserConsented(userId, termsId);

        // then
        verify(jpaRepository).hasUserConsented(userId, termsId);
        assertThat(result).isTrue();
    }
}