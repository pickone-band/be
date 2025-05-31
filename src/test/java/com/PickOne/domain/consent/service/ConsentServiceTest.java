package com.PickOne.domain.consent.service;

import com.PickOne.domain.consent.model.domain.Consent;
import com.PickOne.domain.consent.repository.ConsentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConsentServiceTest {

    private ConsentRepository consentRepository;
    private ConsentService consentService;

    @BeforeEach
    void setUp() {
        consentRepository = mock(ConsentRepository.class);
        consentService = new ConsentService(consentRepository);
    }

    @Test
    @DisplayName("동의 저장 - 성공")
    void saveConsent_success() {
        Consent consent = new Consent(1L, 2L, true, LocalDateTime.now());
        when(consentRepository.save(any())).thenReturn(consent);

        Consent result = consentService.saveConsent(consent);

        assertThat(result).isEqualTo(consent);
    }

    @Test
    @DisplayName("사용자 동의 목록 조회 - 성공")
    void getUserConsents_success() {
        List<Consent> consents = List.of(
                new Consent(1L, 101L, true, LocalDateTime.now()),
                new Consent(1L, 102L, false, LocalDateTime.now())
        );
        when(consentRepository.findByUserId(1L)).thenReturn(consents);

        List<Consent> result = consentService.getUserConsents(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("특정 약관 동의 여부 확인 - 동의한 경우")
    void hasConsented_true() {
        when(consentRepository.findByUserIdAndTermsId(1L, 101L))
                .thenReturn(Optional.of(new Consent(1L, 101L, true, LocalDateTime.now())));

        assertThat(consentService.hasConsented(1L, 101L)).isTrue();
    }

    @Test
    @DisplayName("특정 약관 동의 여부 확인 - 동의하지 않은 경우")
    void hasConsented_false() {
        when(consentRepository.findByUserIdAndTermsId(1L, 101L))
                .thenReturn(Optional.empty());

        assertThat(consentService.hasConsented(1L, 101L)).isFalse();
    }

    @Test
    @DisplayName("동의 삭제 - 성공")
    void deleteConsent_success() {
        doNothing().when(consentRepository).deleteByUserIdAndTermsId(1L, 101L);

        consentService.deleteConsent(1L, 101L);

        verify(consentRepository).deleteByUserIdAndTermsId(1L, 101L);
    }
}
