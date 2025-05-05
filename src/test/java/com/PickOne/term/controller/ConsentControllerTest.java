package com.PickOne.term.controller;

import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.BusinessException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
class ConsentControllerTest {

    @Mock
    private UserConsentService userConsentService;

    @InjectMocks
    private ConsentController consentController;

    private final Long userId = 1L;
    private final Long termsId = 2L;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Mock 인증 설정
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("1"); // userId를 문자열로 반환
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // 각 테스트 후 SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    /**
     * 약관 동의 생성/업데이트 테스트
     */
    @Test
    @DisplayName("약관 동의 생성/업데이트 테스트 - 성공 케이스")
    void saveConsent_Success() {
        // given
        ConsentRequest request = new ConsentRequest(termsId, true);

        TermConsent expectedConsent = mock(TermConsent.class);
        when(userConsentService.saveConsent(any(TermConsent.class))).thenReturn(expectedConsent);

        // when
        ResponseEntity<BaseResponse<TermConsent>> response = consentController.saveConsent(request);

        // then
        verify(userConsentService).saveConsent(any(TermConsent.class));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult()).isEqualTo(expectedConsent);
    }

    /**
     * 약관 동의 여부 확인 테스트
     */
    @Test
    @DisplayName("약관 동의 여부 확인 테스트 - 동의한 경우")
    void hasConsented_WhenConsented() {
        // given
        // SecurityContext 설정
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userId.toString());

        // 서비스 호출 설정
        when(userConsentService.hasUserConsented(userId, termsId)).thenReturn(true);

        // when
        ResponseEntity<BaseResponse<ConsentCheckResponse>> response = consentController.hasConsented(termsId);

        // then
        verify(userConsentService).hasUserConsented(userId, termsId);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult().consented()).isTrue();

        // 테스트 완료 후 컨텍스트 정리
        SecurityContextHolder.clearContext();
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

        when(userConsentService.getUserConsents(userId)).thenReturn(consentList);

        // when
        ResponseEntity<BaseResponse<List<TermConsent>>> response = consentController.getUserConsents();

        // then
        verify(userConsentService).getUserConsents(userId);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult()).hasSize(2);
        assertThat(response.getBody().getResult()).containsExactly(consent1, consent2);
    }

    /**
     * 인증되지 않은 사용자 예외 테스트
     */
    @Test
    @DisplayName("인증되지 않은 사용자 예외 테스트")
    void unauthenticatedUser() {
        // given
        SecurityContextHolder.clearContext(); // 인증 정보 제거

        // when & then
        assertThatThrownBy(() -> consentController.getUserConsents())
                .isInstanceOf(BusinessException.class);
    }
}