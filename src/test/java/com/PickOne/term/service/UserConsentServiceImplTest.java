package com.PickOne.term.service;

import com.PickOne.domain.term.model.domain.TermConsent;
import com.PickOne.domain.term.repository.TermConsentRepository;
import com.PickOne.domain.term.service.UserConsentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * UserConsentServiceImpl 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class UserConsentServiceImplTest {

    @Mock
    private TermConsentRepository termConsentRepository;

    @InjectMocks
    private UserConsentServiceImpl userConsentService;

    private final Long userId = 1L;
    private final Long termsId = 2L;
    private final LocalDateTime now = LocalDateTime.now();

    /**
     * 약관 동의 저장 테스트
     */
    @Test
    @DisplayName("약관 동의 저장 테스트")
    void saveConsent_Success() {
        // given
        TermConsent consent = mock(TermConsent.class);
        when(termConsentRepository.save(consent)).thenReturn(consent);

        // when
        TermConsent result = userConsentService.saveConsent(consent);

        // then
        verify(termConsentRepository).save(consent);
        assertThat(result).isEqualTo(consent);
    }

    /**
     * 약관 동의 여부 확인 테스트 - 동의한 경우
     */
    @Test
    @DisplayName("약관 동의 여부 확인 테스트 - 동의한 경우")
    void hasUserConsented_True() {
        // given
        when(termConsentRepository.hasUserConsented(userId, termsId)).thenReturn(true);

        // when
        boolean result = userConsentService.hasUserConsented(userId, termsId);

        // then
        verify(termConsentRepository).hasUserConsented(userId, termsId);
        assertThat(result).isTrue();
    }

    /**
     * 약관 동의 여부 확인 테스트 - 동의하지 않은 경우
     */
    @Test
    @DisplayName("약관 동의 여부 확인 테스트 - 동의하지 않은 경우")
    void hasUserConsented_False() {
        // given
        when(termConsentRepository.hasUserConsented(userId, termsId)).thenReturn(false);

        // when
        boolean result = userConsentService.hasUserConsented(userId, termsId);

        // then
        verify(termConsentRepository).hasUserConsented(userId, termsId);
        assertThat(result).isFalse();
    }

    /**
     * 사용자의 모든 약관 동의 정보 조회 테스트
     */
    @Test
    @DisplayName("사용자의 모든 약관 동의 정보 조회 테스트")
    void getUserConsents_Success() {
        // given
        TermConsent consent1 = mock(TermConsent.class);
        TermConsent consent2 = mock(TermConsent.class);
        List<TermConsent> consentList = Arrays.asList(consent1, consent2);

        when(termConsentRepository.findAllByUserId(userId)).thenReturn(consentList);

        // when
        List<TermConsent> results = userConsentService.getUserConsents(userId);

        // then
        verify(termConsentRepository).findAllByUserId(userId);
        assertThat(results).hasSize(2);
        assertThat(results).containsExactly(consent1, consent2);
    }
}