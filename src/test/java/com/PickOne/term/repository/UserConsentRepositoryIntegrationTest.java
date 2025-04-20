package com.PickOne.term.repository;

import com.PickOne.common.config.AuditConfig;
import com.PickOne.common.config.SecurityConfig;
import com.PickOne.term.model.domain.*;
import com.PickOne.term.model.entity.TermsEntity;
import com.PickOne.term.model.entity.UserConsentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({AuditConfig.class, SecurityConfig.class, UserConsentRepositoryImpl.class})
class UserConsentRepositoryIntegrationTest {

    @Autowired
    private UserConsentRepository userConsentRepository;

    @Autowired
    private JpaUserConsentRepository jpaUserConsentRepository;

    @Autowired
    private JpaTermsRepository jpaTermsRepository;

    private TermsEntity termsEntity;
    private UserConsentEntity userConsentEntity;
    private final Long USER_ID = 1L;
    private final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void setUp() {
        jpaUserConsentRepository.deleteAll();
        jpaTermsRepository.deleteAll();

        termsEntity = saveTerms("서비스 이용약관", "서비스 약관 내용", TermsType.SERVICE, "1.0.0", TODAY, true);
        userConsentEntity = saveUserConsent(USER_ID, termsEntity.getId(), TODAY, true);
    }

    @Test
    @DisplayName("사용자 동의 조회 통합 테스트")
    void findByUserIdAndTermsId_ShouldReturnCorrectConsent() {
        // when
        Optional<UserConsent> foundConsent = userConsentRepository.findByUserIdAndTermsId(USER_ID, termsEntity.getId());

        // then
        assertThat(foundConsent).isPresent();
        assertThat(foundConsent.get().getUserId()).isEqualTo(USER_ID);
        assertThat(foundConsent.get().getTermsId()).isEqualTo(termsEntity.getId());
        assertThat(foundConsent.get().isConsented()).isTrue();
    }

    @Test
    @DisplayName("사용자 동의 저장 통합 테스트")
    void save_ShouldPersistUserConsentCorrectly() {
        // given
        UserConsent newConsent = UserConsent.of(2L, termsEntity.getId(), TODAY, false);

        // when
        UserConsent savedConsent = userConsentRepository.save(newConsent);

        // then
        assertThat(savedConsent).isNotNull();
        assertThat(savedConsent.getUserId()).isEqualTo(2L);
        assertThat(savedConsent.getTermsId()).isEqualTo(termsEntity.getId());
        assertThat(savedConsent.isConsented()).isFalse();

        // DB 저장 확인
        Optional<UserConsentEntity> foundEntity = jpaUserConsentRepository.findByUserIdAndTermsId(2L, termsEntity.getId());
        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().isConsented()).isFalse();
    }

    @Test
    @DisplayName("사용자별 동의 목록 조회 통합 테스트")
    void findAllByUserId_ShouldReturnAllUserConsents() {
        // given
        TermsEntity privacyTermsEntity = saveTerms(
                "개인정보 처리방침", "개인정보 약관 내용", TermsType.PRIVACY, "1.0.0", TODAY, true);
        saveUserConsent(USER_ID, privacyTermsEntity.getId(), TODAY, true);

        // when
        List<UserConsent> userConsents = userConsentRepository.findAllByUserId(USER_ID);

        // then
        assertThat(userConsents).hasSize(2);
        assertThat(hasConsentForTerms(userConsents, termsEntity.getId())).isTrue();
        assertThat(hasConsentForTerms(userConsents, privacyTermsEntity.getId())).isTrue();
    }

    @Test
    @DisplayName("약관별 동의 사용자 목록 조회 통합 테스트")
    void findAllByTermsId_ShouldReturnAllUsersConsents() {
        // given
        saveUserConsent(2L, termsEntity.getId(), TODAY, true);

        // when
        List<UserConsent> termsConsents = userConsentRepository.findAllByTermsId(termsEntity.getId());

        // then
        assertThat(termsConsents).hasSize(2);
        assertThat(hasConsentForUser(termsConsents, USER_ID)).isTrue();
        assertThat(hasConsentForUser(termsConsents, 2L)).isTrue();
    }

    @Test
    @DisplayName("동의 여부 확인 통합 테스트")
    void hasUserConsented_ShouldReturnCorrectConsentStatus() {
        // given
        saveUserConsent(3L, termsEntity.getId(), TODAY, false);

        // when
        boolean user1Consented = userConsentRepository.hasUserConsented(USER_ID, termsEntity.getId());
        boolean user3Consented = userConsentRepository.hasUserConsented(3L, termsEntity.getId());
        boolean nonExistingUserConsented = userConsentRepository.hasUserConsented(999L, termsEntity.getId());

        // then
        assertThat(user1Consented).isTrue();
        assertThat(user3Consented).isFalse();
        assertThat(nonExistingUserConsented).isFalse();
    }

    @Test
    @DisplayName("동의 약관 수 카운트 통합 테스트")
    void countConsentedTermsByUserIdAndTermsIds_ShouldReturnCorrectCount() {
        // given
        TermsEntity privacyTermsEntity = saveTerms(
                "개인정보 처리방침", "개인정보 약관 내용", TermsType.PRIVACY, "1.0.0", TODAY, true);

        TermsEntity marketingTermsEntity = saveTerms(
                "마케팅 수신 동의", "마케팅 약관 내용", TermsType.MARKETING, "1.0.0", TODAY, false);

        saveUserConsent(USER_ID, privacyTermsEntity.getId(), TODAY, true);
        saveUserConsent(USER_ID, marketingTermsEntity.getId(), TODAY, false);

        // when
        long countAll = userConsentRepository.countConsentedTermsByUserIdAndTermsIds(
                USER_ID,
                Arrays.asList(termsEntity.getId(), privacyTermsEntity.getId(), marketingTermsEntity.getId()));

        long countRequired = userConsentRepository.countConsentedTermsByUserIdAndTermsIds(
                USER_ID,
                Arrays.asList(termsEntity.getId(), privacyTermsEntity.getId()));

        // then
        assertThat(countAll).isEqualTo(2); // 서비스, 개인정보는 동의했지만 마케팅은 동의 안함
        assertThat(countRequired).isEqualTo(2); // 필수 약관(서비스, 개인정보)은 모두 동의
    }

    @Test
    @DisplayName("약관에 동의한 사용자 ID 목록 조회 통합 테스트")
    void findUserIdsByTermsIdAndConsented_ShouldReturnCorrectUserIds() {
        // given
        saveUserConsent(2L, termsEntity.getId(), TODAY, true);
        saveUserConsent(3L, termsEntity.getId(), TODAY, false);

        // when
        List<Long> consentedUserIds = userConsentRepository.findUserIdsByTermsIdAndConsented(termsEntity.getId());

        // then
        assertThat(consentedUserIds).hasSize(2);
        assertThat(consentedUserIds).contains(USER_ID, 2L);
        assertThat(consentedUserIds).doesNotContain(3L); // 동의하지 않은 사용자는 제외
    }

    // 헬퍼 메서드
    private TermsEntity saveTerms(String title, String content, TermsType type,
                                  String version, LocalDate effectiveDate, boolean required) {
        Terms terms = Terms.create(
                Title.of(title),
                Content.of(content),
                type,
                Version.of(version),
                EffectiveDate.of(effectiveDate),
                Required.of(required)
        );
        return jpaTermsRepository.save(TermsEntity.from(terms));
    }

    private UserConsentEntity saveUserConsent(Long userId, Long termsId, LocalDate consentDate, boolean isConsented) {
        UserConsent userConsent = UserConsent.of(userId, termsId, consentDate, isConsented);
        return jpaUserConsentRepository.save(UserConsentEntity.from(userConsent));
    }

    private boolean hasConsentForTerms(List<UserConsent> consents, Long termsId) {
        return consents.stream()
                .anyMatch(consent -> consent.getTermsId().equals(termsId));
    }

    private boolean hasConsentForUser(List<UserConsent> consents, Long userId) {
        return consents.stream()
                .anyMatch(consent -> consent.getUserId().equals(userId));
    }
}