package com.PickOne.term.service;

import com.PickOne.term.model.domain.Terms;
import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.domain.UserConsent;
import com.PickOne.term.repository.userConsent.UserConsentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserConsentServiceTest {

    @Mock
    private UserConsentRepository userConsentRepository;

    @Mock
    private TermsService termsService;

    @InjectMocks
    private UserConsentServiceImpl userConsentService;

    private final Long USER_ID = 1L;
    private final Long TERMS_ID = 2L;

    @Test
    @DisplayName("약관 동의 생성 테스트")
    void createOrUpdateConsent_WithNewConsent_ShouldCreateConsent() {
        // given
        Terms terms = mock(Terms.class);
        UserConsent userConsent = mock(UserConsent.class);

        when(termsService.getTermsById(TERMS_ID)).thenReturn(terms);
        when(userConsentRepository.findByUserIdAndTermsId(USER_ID, TERMS_ID)).thenReturn(Optional.empty());
        when(userConsentRepository.save(any(UserConsent.class))).thenReturn(userConsent);

        // when
        UserConsent createdConsent = userConsentService.createOrUpdateConsent(USER_ID, TERMS_ID, true);

        // then
        assertThat(createdConsent).isEqualTo(userConsent);
        verify(termsService).getTermsById(TERMS_ID);
        verify(userConsentRepository).findByUserIdAndTermsId(USER_ID, TERMS_ID);
        verify(userConsentRepository).save(any(UserConsent.class));
    }

    @Test
    @DisplayName("약관 동의 업데이트 테스트")
    void createOrUpdateConsent_WithExistingConsent_ShouldUpdateConsent() {
        // given
        Terms terms = mock(Terms.class);
        UserConsent existingConsent = mock(UserConsent.class);
        UserConsent updatedConsent = mock(UserConsent.class);

        when(termsService.getTermsById(TERMS_ID)).thenReturn(terms);
        when(userConsentRepository.findByUserIdAndTermsId(USER_ID, TERMS_ID)).thenReturn(Optional.of(existingConsent));
        when(userConsentRepository.save(any(UserConsent.class))).thenReturn(updatedConsent);

        // when
        UserConsent result = userConsentService.createOrUpdateConsent(USER_ID, TERMS_ID, false);

        // then
        assertThat(result).isEqualTo(updatedConsent);
        verify(termsService).getTermsById(TERMS_ID);
        verify(userConsentRepository).findByUserIdAndTermsId(USER_ID, TERMS_ID);
        verify(userConsentRepository).save(any(UserConsent.class));
    }

    @Test
    @DisplayName("존재하지 않는 약관에 동의 시 예외 발생 테스트")
    void createOrUpdateConsent_WithNonExistingTerms_ShouldThrowException() {
        // given
        when(termsService.getTermsById(TERMS_ID)).thenThrow(new NoSuchElementException("약관이 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> userConsentService.createOrUpdateConsent(USER_ID, TERMS_ID, true))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("약관이 존재하지 않습니다");
    }

    @Test
    @DisplayName("사용자 동의 여부 확인 테스트")
    void hasUserConsented_ShouldReturnCorrectConsentStatus() {
        // given
        when(userConsentRepository.hasUserConsented(USER_ID, TERMS_ID)).thenReturn(true);

        // when
        boolean hasConsented = userConsentService.hasUserConsented(USER_ID, TERMS_ID);

        // then
        assertThat(hasConsented).isTrue();
        verify(userConsentRepository).hasUserConsented(USER_ID, TERMS_ID);
    }

    @Test
    @DisplayName("특정 유형의 약관 동의 확인 테스트")
    void hasUserConsentedToType_WithValidTerms_ShouldCheckConsent() {
        // given
        Terms terms = mock(Terms.class);
        when(terms.getId()).thenReturn(TERMS_ID);

        when(termsService.getCurrentlyEffectiveTermsByType(TermsType.SERVICE)).thenReturn(terms);
        when(userConsentRepository.hasUserConsented(USER_ID, TERMS_ID)).thenReturn(true);

        // when
        boolean hasConsented = userConsentService.hasUserConsentedToType(USER_ID, TermsType.SERVICE);

        // then
        assertThat(hasConsented).isTrue();
        verify(termsService).getCurrentlyEffectiveTermsByType(TermsType.SERVICE);
        verify(userConsentRepository).hasUserConsented(USER_ID, TERMS_ID);
    }

    @Test
    @DisplayName("유효한 약관이 없을 때 동의 확인 테스트")
    void hasUserConsentedToType_WithNoValidTerms_ShouldReturnFalse() {
        // given
        when(termsService.getCurrentlyEffectiveTermsByType(TermsType.MARKETING))
                .thenThrow(new NoSuchElementException("유효한 약관이 존재하지 않습니다."));

        // when
        boolean hasConsented = userConsentService.hasUserConsentedToType(USER_ID, TermsType.MARKETING);

        // then
        assertThat(hasConsented).isFalse();
        verify(termsService).getCurrentlyEffectiveTermsByType(TermsType.MARKETING);
        verify(userConsentRepository, never()).hasUserConsented(anyLong(), anyLong());
    }

    @Test
    @DisplayName("필수 약관 모두 동의 확인 테스트")
    void hasUserConsentedToAllRequiredTerms_WithAllConsented_ShouldReturnTrue() {
        // given
        List<Terms> requiredTerms = createMockTermsList(2);
        when(termsService.getAllRequiredTerms()).thenReturn(requiredTerms);
        when(userConsentRepository.countConsentedTermsByUserIdAndTermsIds(eq(USER_ID), anyList())).thenReturn(2L);

        // when
        boolean hasConsentedToAll = userConsentService.hasUserConsentedToAllRequiredTerms(USER_ID);

        // then
        assertThat(hasConsentedToAll).isTrue();
        verify(termsService).getAllRequiredTerms();
        verify(userConsentRepository).countConsentedTermsByUserIdAndTermsIds(eq(USER_ID), anyList());
    }

    @Test
    @DisplayName("필수 약관 일부만 동의한 경우 테스트")
    void hasUserConsentedToAllRequiredTerms_WithPartialConsent_ShouldReturnFalse() {
        // given
        List<Terms> requiredTerms = createMockTermsList(2);
        when(termsService.getAllRequiredTerms()).thenReturn(requiredTerms);
        when(userConsentRepository.countConsentedTermsByUserIdAndTermsIds(eq(USER_ID), anyList())).thenReturn(1L);

        // when
        boolean hasConsentedToAll = userConsentService.hasUserConsentedToAllRequiredTerms(USER_ID);

        // then
        assertThat(hasConsentedToAll).isFalse();
        verify(termsService).getAllRequiredTerms();
        verify(userConsentRepository).countConsentedTermsByUserIdAndTermsIds(eq(USER_ID), anyList());
    }

    @Test
    @DisplayName("마케팅 정보 수신 동의 업데이트 테스트")
    void updateMarketingConsent_ShouldUpdateMarketingTermsConsent() {
        // given
        Long marketingTermsId = 3L;
        Terms marketingTerms = mock(Terms.class);
        UserConsent userConsent = mock(UserConsent.class);

        when(marketingTerms.getId()).thenReturn(marketingTermsId);

        when(termsService.getCurrentlyEffectiveTermsByType(TermsType.MARKETING)).thenReturn(marketingTerms);
        when(termsService.getTermsById(marketingTermsId)).thenReturn(marketingTerms);
        when(userConsentRepository.findByUserIdAndTermsId(USER_ID, marketingTermsId)).thenReturn(Optional.empty());
        when(userConsentRepository.save(any(UserConsent.class))).thenReturn(userConsent);

        // when
        userConsentService.updateMarketingConsent(USER_ID, true);

        // then
        verify(termsService).getCurrentlyEffectiveTermsByType(TermsType.MARKETING);
        verify(userConsentRepository).save(any(UserConsent.class));
    }

    // 헬퍼 메서드
    private List<Terms> createMockTermsList(int count) {
        Terms[] termsArray = new Terms[count];
        for (int i = 0; i < count; i++) {
            Terms mockTerms = mock(Terms.class);
            when(mockTerms.getId()).thenReturn((long)(i + 1));
            termsArray[i] = mockTerms;
        }
        return Arrays.asList(termsArray);
    }
}