package com.PickOne.term.controller;

import com.PickOne.term.controller.dto.userConsent.MarketingConsentRequest;
import com.PickOne.term.controller.dto.userConsent.ConsentCheckResponse;
import com.PickOne.term.controller.dto.userConsent.ConsentRequest;
import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.domain.UserConsent;
import com.PickOne.term.service.UserConsentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * UserConsentController에 대한 단위 테스트 (Mockito 사용)
 * - SecurityContext를 직접 설정하여 getCurrentUserId() 메서드 테스트
 */
@ExtendWith(MockitoExtension.class)
public class UserConsentControllerTest {

    @Mock
    private UserConsentService userConsentService;

    @InjectMocks
    private UserConsentController userConsentController;

    private UserConsent testUserConsent;
    private List<UserConsent> testUserConsentList;
    private final Long TEST_USER_ID = 100L;

    @BeforeEach
    void setUp() {
        // Spring Security 컨텍스트 설정 (테스트용)
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(TEST_USER_ID, null, "ROLE_USER")
        );

        // 테스트용 사용자 동의 객체 생성 - 팩토리 메서드 사용
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        testUserConsent = UserConsent.of(
                TEST_USER_ID,
                1L,
                today,
                true
        );

        UserConsent testUserConsent2 = UserConsent.of(
                TEST_USER_ID,
                2L,
                today,
                true
        );

        testUserConsentList = Arrays.asList(testUserConsent, testUserConsent2);
    }

    @Test
    @DisplayName("약관 동의 생성 성공 테스트")
    void createOrUpdateConsent_ShouldCreateAndReturnConsent() {
        // Given
        ConsentRequest request = new ConsentRequest(1L, true);
        given(userConsentService.createOrUpdateConsent(anyLong(), anyLong(), anyBoolean()))
                .willReturn(testUserConsent);

        // When
        ResponseEntity<UserConsent> response = userConsentController.createOrUpdateConsent(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(response.getBody().getTermsId()).isEqualTo(1L);
        assertThat(response.getBody().isConsented()).isTrue();

        verify(userConsentService).createOrUpdateConsent(TEST_USER_ID, 1L, true);
    }

    @Test
    @DisplayName("약관 동의 여부 확인 성공 테스트")
    void hasUserConsented_ShouldReturnConsentStatus() {
        // Given
        Long termsId = 1L;
        given(userConsentService.hasUserConsented(anyLong(), anyLong())).willReturn(true);

        // When
        ResponseEntity<ConsentCheckResponse> response = userConsentController.hasUserConsented(termsId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().consented()).isTrue();

        verify(userConsentService).hasUserConsented(TEST_USER_ID, termsId);
    }

    @Test
    @DisplayName("약관 유형별 동의 여부 확인 성공 테스트")
    void hasUserConsentedToType_ShouldReturnConsentStatus() {
        // Given
        TermsType type = TermsType.SERVICE;
        given(userConsentService.hasUserConsentedToType(anyLong(), any(TermsType.class))).willReturn(true);

        // When
        ResponseEntity<ConsentCheckResponse> response = userConsentController.hasUserConsentedToType(type);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().consented()).isTrue();

        verify(userConsentService).hasUserConsentedToType(TEST_USER_ID, type);
    }

    @Test
    @DisplayName("필수 약관 전체 동의 여부 확인 성공 테스트")
    void hasUserConsentedToAllRequiredTerms_ShouldReturnConsentStatus() {
        // Given
        given(userConsentService.hasUserConsentedToAllRequiredTerms(anyLong())).willReturn(true);

        // When
        ResponseEntity<ConsentCheckResponse> response = userConsentController.hasUserConsentedToAllRequiredTerms();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().consented()).isTrue();

        verify(userConsentService).hasUserConsentedToAllRequiredTerms(TEST_USER_ID);
    }

    @Test
    @DisplayName("사용자의 모든 동의 정보 조회 성공 테스트")
    void getAllUserConsents_ShouldReturnConsentList() {
        // Given
        given(userConsentService.getAllUserConsents(anyLong())).willReturn(testUserConsentList);

        // When
        ResponseEntity<List<UserConsent>> response = userConsentController.getAllUserConsents();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);
        assertThat(response.getBody().get(0).getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(response.getBody().get(1).getUserId()).isEqualTo(TEST_USER_ID);

        verify(userConsentService).getAllUserConsents(TEST_USER_ID);
    }

    @Test
    @DisplayName("특정 약관 동의 정보 조회 성공 테스트")
    void getUserConsent_ShouldReturnConsent() {
        // Given
        Long termsId = 1L;
        given(userConsentService.getUserConsent(anyLong(), anyLong())).willReturn(testUserConsent);

        // When
        ResponseEntity<UserConsent> response = userConsentController.getUserConsent(termsId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(response.getBody().getTermsId()).isEqualTo(1L);

        verify(userConsentService).getUserConsent(TEST_USER_ID, termsId);
    }

    @Test
    @DisplayName("마케팅 수신 동의 업데이트 성공 테스트")
    void updateMarketingConsent_ShouldReturnUpdatedConsent() {
        // Given
        MarketingConsentRequest request = new MarketingConsentRequest(true);
        given(userConsentService.updateMarketingConsent(anyLong(), anyBoolean())).willReturn(testUserConsent);

        // When
        ResponseEntity<UserConsent> response = userConsentController.updateMarketingConsent(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isConsented()).isTrue();

        verify(userConsentService).updateMarketingConsent(TEST_USER_ID, true);
    }

    @Test
    @DisplayName("마케팅 수신 동의 여부 확인 성공 테스트")
    void hasMarketingConsent_ShouldReturnConsentStatus() {
        // Given
        given(userConsentService.hasMarketingConsent(anyLong())).willReturn(true);

        // When
        ResponseEntity<ConsentCheckResponse> response = userConsentController.hasMarketingConsent();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().consented()).isTrue();

        verify(userConsentService).hasMarketingConsent(TEST_USER_ID);
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 SecurityContext 정리
        SecurityContextHolder.clearContext();
    }
}