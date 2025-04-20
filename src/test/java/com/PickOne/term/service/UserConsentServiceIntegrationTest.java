package com.PickOne.term.service;

import com.PickOne.term.model.domain.Terms;
import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.domain.UserConsent;
import com.PickOne.term.model.domain.Version;
import com.PickOne.term.model.entity.TermsEntity;
import com.PickOne.term.model.entity.UserConsentEntity;
import com.PickOne.term.repository.terms.JpaTermsRepository;
import com.PickOne.term.repository.userConsent.JpaUserConsentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserConsentServiceIntegrationTest {

    @Autowired
    private TermsService termsService;

    @Autowired
    private UserConsentService userConsentService;

    @Autowired
    private JpaTermsRepository jpaTermsRepository;

    @Autowired
    private JpaUserConsentRepository jpaUserConsentRepository;

    private final String TEST_USER = "test-user";
    private final Long USER_ID = 1L;
    private final LocalDate TODAY = LocalDate.now();

    private Terms serviceTerms;
    private Terms privacyTerms;
    private Terms marketingTerms;

    @BeforeEach
    void setUp() {
        // 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(TEST_USER, null, "ROLE_USER"));

        // 테스트 데이터 초기화
        jpaUserConsentRepository.deleteAll();
        jpaTermsRepository.deleteAll();

        // 테스트용 약관 생성
        serviceTerms = createTerms("서비스 이용약관", "서비스 약관 내용", TermsType.SERVICE, true);
        privacyTerms = createTerms("개인정보 처리방침", "개인정보 약관 내용", TermsType.PRIVACY, true);
        marketingTerms = createTerms("마케팅 수신 동의", "마케팅 약관 내용", TermsType.MARKETING, false);
    }

    @Test
    @DisplayName("약관 동의 생성 서비스 통합 테스트")
    void createOrUpdateConsent_ShouldCreateNewConsent() {
        // when
        UserConsent consent = userConsentService.createOrUpdateConsent(USER_ID, serviceTerms.getId(), true);

        // then
        assertThat(consent).isNotNull();
        assertThat(consent.getUserId()).isEqualTo(USER_ID);
        assertThat(consent.getTermsId()).isEqualTo(serviceTerms.getId());
        assertThat(consent.isConsented()).isTrue();
        assertThat(consent.getConsentDate()).isEqualTo(TODAY);

        // DB 저장 확인
        UserConsentEntity entity = jpaUserConsentRepository.findByUserIdAndTermsId(USER_ID, serviceTerms.getId())
                .orElseThrow();
        assertThat(entity.isConsented()).isTrue();
    }

    @AfterEach
    void tearDown() {
        // 테스트 종료 후 데이터 정리
        jpaUserConsentRepository.deleteAll();
        jpaTermsRepository.deleteAll();
    }

    @Test
    @DisplayName("약관 동의 상태 변경 서비스 통합 테스트")
    void createOrUpdateConsent_WithExistingConsent_ShouldUpdateConsent() {
        // given
        userConsentService.createOrUpdateConsent(USER_ID, serviceTerms.getId(), true);

        // when
        UserConsent updatedConsent = userConsentService.createOrUpdateConsent(USER_ID, serviceTerms.getId(), false);

        // then
        assertThat(updatedConsent.isConsented()).isFalse();

        // DB 상태 확인
        UserConsentEntity entity = jpaUserConsentRepository.findByUserIdAndTermsId(USER_ID, serviceTerms.getId())
                .orElseThrow();
        assertThat(entity.isConsented()).isFalse();
    }

    @Test
    @DisplayName("사용자별 동의 목록 조회 서비스 통합 테스트")
    void getAllUserConsents_ShouldReturnAllUserConsents() {
        // given
        createThreeConsents();

        // when
        List<UserConsent> consents = userConsentService.getAllUserConsents(USER_ID);

        // then
        assertThat(consents).hasSize(3);

        assertThat(hasTermsConsent(consents, serviceTerms.getId(), true)).isTrue();
        assertThat(hasTermsConsent(consents, privacyTerms.getId(), true)).isTrue();
        assertThat(hasTermsConsent(consents, marketingTerms.getId(), false)).isTrue();
    }

    @Test
    @DisplayName("동의 여부 확인 서비스 통합 테스트")
    void hasUserConsented_ShouldReturnCorrectConsentStatus() {
        // given
        userConsentService.createOrUpdateConsent(USER_ID, serviceTerms.getId(), true);

        // when
        boolean hasConsented = userConsentService.hasUserConsented(USER_ID, serviceTerms.getId());
        boolean hasConsentedPrivacy = userConsentService.hasUserConsented(USER_ID, privacyTerms.getId());

        // then
        assertThat(hasConsented).isTrue();
        assertThat(hasConsentedPrivacy).isFalse();
    }

    @Test
    @DisplayName("약관 유형별 동의 여부 확인 서비스 통합 테스트")
    void hasUserConsentedToType_ShouldReturnCorrectConsentStatus() {
        // given
        userConsentService.createOrUpdateConsent(USER_ID, serviceTerms.getId(), true);

        // when
        boolean hasConsentedService = userConsentService.hasUserConsentedToType(USER_ID, TermsType.SERVICE);
        boolean hasConsentedPrivacy = userConsentService.hasUserConsentedToType(USER_ID, TermsType.PRIVACY);

        // then
        assertThat(hasConsentedService).isTrue();
        assertThat(hasConsentedPrivacy).isFalse();
    }

    @Test
    @DisplayName("필수 약관 전체 동의 확인 서비스 통합 테스트")
    void hasUserConsentedToAllRequiredTerms_ShouldCheckRequiredTermsStatus() {
        // given - 서비스 약관만 동의
        userConsentService.createOrUpdateConsent(USER_ID, serviceTerms.getId(), true);

        // when
        boolean hasConsentedAllRequired1 = userConsentService.hasUserConsentedToAllRequiredTerms(USER_ID);

        // then
        assertThat(hasConsentedAllRequired1).isFalse();

        // given - 모든 필수 약관 동의
        userConsentService.createOrUpdateConsent(USER_ID, privacyTerms.getId(), true);

        // when
        boolean hasConsentedAllRequired2 = userConsentService.hasUserConsentedToAllRequiredTerms(USER_ID);

        // then
        assertThat(hasConsentedAllRequired2).isTrue();
    }

    @Test
    @DisplayName("마케팅 약관 동의 업데이트 서비스 통합 테스트")
    void updateMarketingConsent_ShouldUpdateMarketingConsent() {
        // when
        UserConsent consent = userConsentService.updateMarketingConsent(USER_ID, true);

        // then
        assertThat(consent).isNotNull();
        assertThat(consent.getTermsId()).isEqualTo(marketingTerms.getId());
        assertThat(consent.isConsented()).isTrue();

        // 마케팅 동의 여부 확인
        boolean hasMarketingConsent = userConsentService.hasMarketingConsent(USER_ID);
        assertThat(hasMarketingConsent).isTrue();
    }

    @Test
    @DisplayName("약관에 동의한 사용자 목록 조회 서비스 통합 테스트")
    void getUsersConsentedToTerms_ShouldReturnUserIdsList() {
        // given
        userConsentService.createOrUpdateConsent(USER_ID, serviceTerms.getId(), true);
        userConsentService.createOrUpdateConsent(2L, serviceTerms.getId(), true);
        userConsentService.createOrUpdateConsent(3L, serviceTerms.getId(), false);

        // when
        List<Long> consentedUsers = userConsentService.getUsersConsentedToTerms(serviceTerms.getId());

        // then
        assertThat(consentedUsers).hasSize(2);
        assertThat(consentedUsers).contains(USER_ID, 2L);
        assertThat(consentedUsers).doesNotContain(3L);
    }

    @Test
    @DisplayName("존재하지 않는 약관 동의 조회 시 예외 발생 서비스 통합 테스트")
    void getUserConsent_WithNonExistingConsent_ShouldThrowException() {
        // when & then
        assertThatThrownBy(() -> userConsentService.getUserConsent(USER_ID, 999L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("동의 정보가 존재하지 않습니다");
    }

    @Test
    @DisplayName("특정 유형의 유효한 약관이 없을 때 동의 확인 서비스 통합 테스트")
    void hasConsentedToType_WithNonExistingTerms_ShouldReturnFalse() {
        // given - DB에서 마케팅 약관 삭제
        deleteTermsFromDb(marketingTerms.getId());

        // when
        boolean hasConsented = userConsentService.hasUserConsentedToType(USER_ID, TermsType.MARKETING);

        // then
        assertThat(hasConsented).isFalse();
    }

    @Test
    @DisplayName("마케팅 약관이 없을 때 마케팅 동의 업데이트 시 예외 발생 서비스 통합 테스트")
    void updateMarketingConsent_WithNoMarketingTerms_ShouldThrowException() {
        // given - DB에서 마케팅 약관 삭제
        deleteTermsFromDb(marketingTerms.getId());

        // when & then
        assertThatThrownBy(() -> userConsentService.updateMarketingConsent(USER_ID, true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("유효한 마케팅 약관이 존재하지 않습니다");
    }

    // 헬퍼 메서드
    private Terms createTerms(String title, String content, TermsType type, boolean required) {
        return termsService.createTerms(
                title, content, type, Version.of("1.0.0"), TODAY, required);
    }

    private void createThreeConsents() {
        userConsentService.createOrUpdateConsent(USER_ID, serviceTerms.getId(), true);
        userConsentService.createOrUpdateConsent(USER_ID, privacyTerms.getId(), true);
        userConsentService.createOrUpdateConsent(USER_ID, marketingTerms.getId(), false);
    }

    private boolean hasTermsConsent(List<UserConsent> consents, Long termsId, boolean isConsented) {
        return consents.stream()
                .anyMatch(c -> c.getTermsId().equals(termsId) && c.isConsented() == isConsented);
    }

    private void deleteTermsFromDb(Long termsId) {
        jpaTermsRepository.delete((TermsEntity) jpaTermsRepository.findById(termsId).orElseThrow());
    }
}