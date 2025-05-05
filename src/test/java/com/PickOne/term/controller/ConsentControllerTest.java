package com.PickOne.term.controller;

import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.term.dto.ConsentCheckResponse;
import com.PickOne.term.dto.ConsentRequest;
import com.PickOne.term.model.domain.TermConsent;
import com.PickOne.term.service.UserConsentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ConsentController 클래스에 대한 단위 테스트
 * 실제 Authentication 객체를 사용하여 인증 관련 테스트 수행
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConsentControllerTest {

    @Mock
    private UserConsentService userConsentService;

    @InjectMocks
    private ConsentController consentController;

    private final Long userId = 1L;
    private final Long termsId = 2L;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        // 실제 SecurityContext 설정
        Authentication authentication = new TestingAuthenticationToken(
                userId.toString(), "credentials",
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        // 각 테스트 후 SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("약관 동의 생성/업데이트 테스트 - 성공 케이스")
    void saveConsent_Success() {
        // given
        ConsentRequest request = new ConsentRequest(termsId, true);

        TermConsent expectedConsent = Mockito.mock(TermConsent.class);
        when(userConsentService.saveConsent(any(TermConsent.class))).thenReturn(expectedConsent);

        // when
        ResponseEntity<BaseResponse<TermConsent>> response = consentController.saveConsent(request);

        // then
        verify(userConsentService).saveConsent(argThat(consent ->
                consent.getUserId().equals(userId) &&
                        consent.getTermsId().equals(termsId)
        ));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult()).isEqualTo(expectedConsent);
    }

    @Test
    @DisplayName("약관 동의 여부 확인 테스트 - 동의한 경우")
    void hasConsented_WhenConsented() {
        // given
        when(userConsentService.hasUserConsented(userId, termsId)).thenReturn(true);

        // when
        ResponseEntity<BaseResponse<ConsentCheckResponse>> response = consentController.hasConsented(termsId);

        // then
        verify(userConsentService).hasUserConsented(userId, termsId);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult().consented()).isTrue();
    }

    @Test
    @DisplayName("사용자의 모든 약관 동의 정보 조회 테스트")
    void getUserConsents_Success() {
        // given
        TermConsent consent1 = Mockito.mock(TermConsent.class);
        TermConsent consent2 = Mockito.mock(TermConsent.class);
        List<TermConsent> consentList = Arrays.asList(consent1, consent2);

        when(userConsentService.getUserConsents(userId)).thenReturn(consentList);

        // when
        ResponseEntity<BaseResponse<List<TermConsent>>> response = consentController.getUserConsents();

        // then
        verify(userConsentService).getUserConsents(userId);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult()).hasSize(2);
        assertThat(response.getBody().getResult()).containsExactly(consent1, consent2);
    }

    @Test
    @DisplayName("인증되지 않은 사용자 예외 테스트")
    void unauthenticatedUser() {
        // given
        SecurityContextHolder.clearContext(); // 인증 정보 제거

        // when & then
        assertThatThrownBy(() -> consentController.getUserConsents())
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED_ACCESS);
    }
}