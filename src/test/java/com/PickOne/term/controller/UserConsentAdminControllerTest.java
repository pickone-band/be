package com.PickOne.term.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.PickOne.term.controller.dto.userConsent.ConsentRequest;
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
 * UserConsentAdminController에 대한 단위 테스트 (Mockito 사용)
 * - SecurityContext를 직접 설정하여 테스트
 */
@ExtendWith(MockitoExtension.class)
public class UserConsentAdminControllerTest {

    @Mock
    private UserConsentService userConsentService;

    @InjectMocks
    private UserConsentAdminController userConsentAdminController;

    private UserConsent testUserConsent;
    private List<UserConsent> testUserConsentList;
    private final Long TEST_USER_ID_1 = 200L;
    private final Long TEST_USER_ID_2 = 201L;
    private final Long TEST_TERMS_ID_1 = 1L;
    private final Long TEST_TERMS_ID_2 = 2L;

    @BeforeEach
    void setUp() {
        // Spring Security 컨텍스트 설정 (테스트용, 관리자 권한)
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("admin", null, "ROLE_ADMIN")
        );

        // 테스트용 사용자 동의 객체 생성 - 팩토리 메서드 사용
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        testUserConsent = UserConsent.of(
                TEST_USER_ID_1,
                TEST_TERMS_ID_1,
                today,
                true
        );

        UserConsent testUserConsent2 = UserConsent.of(
                TEST_USER_ID_2,
                TEST_TERMS_ID_2,
                today,
                true
        );

        testUserConsentList = Arrays.asList(testUserConsent, testUserConsent2);
    }

    @Test
    @DisplayName("관리자가 사용자 약관 동의 생성/업데이트 성공 테스트")
    void adminCreateOrUpdateConsent_ShouldCreateAndReturnConsent() {
        // Given
        ConsentRequest request = new ConsentRequest(TEST_TERMS_ID_1, true);
        given(userConsentService.createOrUpdateConsent(anyLong(), anyLong(), anyBoolean()))
                .willReturn(testUserConsent);

        // When
        ResponseEntity<UserConsent> response =
                userConsentAdminController.adminCreateOrUpdateConsent(TEST_USER_ID_1, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(TEST_USER_ID_1);
        assertThat(response.getBody().getTermsId()).isEqualTo(TEST_TERMS_ID_1);
        assertThat(response.getBody().isConsented()).isTrue();

        verify(userConsentService).createOrUpdateConsent(TEST_USER_ID_1, TEST_TERMS_ID_1, true);
    }

    @Test
    @DisplayName("관리자가 특정 사용자의 모든 약관 동의 정보 조회 성공 테스트")
    void getAllUserConsentsForAdmin_ShouldReturnConsentList() {
        // Given
        given(userConsentService.getAllUserConsents(anyLong())).willReturn(testUserConsentList);

        // When
        ResponseEntity<List<UserConsent>> response = userConsentAdminController.getAllUserConsentsForAdmin(TEST_USER_ID_1);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);
        assertThat(response.getBody().get(0).getUserId()).isEqualTo(TEST_USER_ID_1);
        assertThat(response.getBody().get(1).getUserId()).isEqualTo(TEST_USER_ID_2);

        verify(userConsentService).getAllUserConsents(TEST_USER_ID_1);
    }

    @Test
    @DisplayName("관리자가 특정 약관에 동의한 사용자 목록 조회 성공 테스트")
    void getUsersConsentedToTerms_ShouldReturnUserIdList() {
        // Given
        List<Long> userIds = Arrays.asList(100L, 200L, 300L);
        given(userConsentService.getUsersConsentedToTerms(anyLong())).willReturn(userIds);

        // When
        ResponseEntity<List<Long>> response = userConsentAdminController.getUsersConsentedToTerms(TEST_TERMS_ID_1);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(3);
        assertThat(response.getBody()).containsExactly(100L, 200L, 300L);

        verify(userConsentService).getUsersConsentedToTerms(TEST_TERMS_ID_1);
    }

    @Test
    @DisplayName("관리자가 특정 사용자의 특정 약관 동의 정보 조회 성공 테스트")
    void getUserConsentByAdmin_ShouldReturnConsent() {
        // Given
        given(userConsentService.getUserConsent(anyLong(), anyLong())).willReturn(testUserConsent);

        // When
        ResponseEntity<UserConsent> response =
                userConsentAdminController.getUserConsentByAdmin(TEST_USER_ID_1, TEST_TERMS_ID_1);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(TEST_USER_ID_1);
        assertThat(response.getBody().getTermsId()).isEqualTo(TEST_TERMS_ID_1);

        verify(userConsentService).getUserConsent(TEST_USER_ID_1, TEST_TERMS_ID_1);
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 SecurityContext 정리
        SecurityContextHolder.clearContext();
    }
}