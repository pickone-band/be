package com.PickOne.term.repository;

import com.PickOne.common.config.AuditConfig;
import com.PickOne.common.config.SecurityConfig;
import com.PickOne.term.model.domain.*;
import com.PickOne.term.model.entity.TermsEntity;
import com.PickOne.term.repository.terms.JpaTermsRepository;
import com.PickOne.term.repository.terms.TermsRepository;
import com.PickOne.term.repository.terms.TermsRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({AuditConfig.class, SecurityConfig.class, TermsRepositoryImpl.class})
class TermsRepositoryIntegrationTest {

    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private JpaTermsRepository jpaTermsRepository;

    private TermsEntity serviceTerms;
    private TermsEntity privacyTerms;
    private final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void setUp() {
        jpaTermsRepository.deleteAll();

        serviceTerms = saveTerms("서비스 이용약관", "서비스 약관 내용", TermsType.SERVICE, "1.0.0", TODAY, true);
        privacyTerms = saveTerms("개인정보 처리방침", "개인정보 약관 내용", TermsType.PRIVACY, "1.0.0", TODAY, true);
    }

    @Test
    @DisplayName("ID로 약관 조회 통합 테스트")
    void findById_ShouldReturnCorrectTerms() {
        // when
        Optional<Terms> foundTerms = termsRepository.findById(serviceTerms.getId());

        // then
        assertThat(foundTerms).isPresent();
        assertThat(foundTerms.get().getTitleValue()).isEqualTo("서비스 이용약관");
        assertThat(foundTerms.get().getType()).isEqualTo(TermsType.SERVICE);
    }

    @Test
    @DisplayName("약관 저장 통합 테스트")
    void save_ShouldPersistTermsCorrectly() {
        // given
        Terms marketingTerms = createTermsDomain(
                "마케팅 수신 동의", "마케팅 약관 내용", TermsType.MARKETING, "1.0.0", TODAY, false);

        // when
        Terms savedTerms = termsRepository.save(marketingTerms);

        // then
        assertThat(savedTerms).isNotNull();
        assertThat(savedTerms.getId()).isNotNull();
        assertThat(savedTerms.getTitleValue()).isEqualTo("마케팅 수신 동의");
        assertThat(savedTerms.getType()).isEqualTo(TermsType.MARKETING);

        // DB 저장 확인
        assertThat(jpaTermsRepository.findById(savedTerms.getId())).isPresent();
    }

    @Test
    @DisplayName("유형별 최신 약관 조회 통합 테스트")
    void findLatestByType_ShouldReturnMostRecentVersion() {
        // given
        saveTerms("서비스 이용약관 개정", "새로운 서비스 약관 내용",
                TermsType.SERVICE, "1.1.0", TODAY.plusDays(1), true);

        // when
        Optional<Terms> latestServiceTerms = termsRepository.findLatestByType(TermsType.SERVICE);

        // then
        assertThat(latestServiceTerms).isPresent();
        assertThat(latestServiceTerms.get().getVersionValue()).isEqualTo("1.1.0");
        assertThat(latestServiceTerms.get().getTitleValue()).isEqualTo("서비스 이용약관 개정");
    }

    @Test
    @DisplayName("현재 유효한 약관 조회 통합 테스트")
    void findCurrentlyEffectiveByType_ShouldReturnCurrentValidTerms() {
        // given
        saveTerms("서비스 이용약관 개정", "미래에 시행될 서비스 약관 내용",
                TermsType.SERVICE, "1.1.0", TODAY.plusDays(10), true);

        // when
        Optional<Terms> currentServiceTerms = termsRepository.findCurrentlyEffectiveByType(
                TermsType.SERVICE, TODAY);

        // then
        assertThat(currentServiceTerms).isPresent();
        assertThat(currentServiceTerms.get().getVersionValue()).isEqualTo("1.0.0");
        assertThat(currentServiceTerms.get().getId()).isEqualTo(serviceTerms.getId());
    }

    @Test
    @DisplayName("필수 동의 약관 전체 조회 통합 테스트")
    void findAllRequiredAndEffective_ShouldReturnOnlyRequiredTerms() {
        // given
        saveTerms("마케팅 수신 동의", "마케팅 약관 내용",
                TermsType.MARKETING, "1.0.0", TODAY, false);

        // when
        List<Terms> requiredTerms = termsRepository.findAllRequiredAndEffective(TODAY);

        // then
        assertThat(requiredTerms).hasSize(2); // 서비스, 개인정보 약관만 필수

        boolean hasServiceTerms = hasTermsOfType(requiredTerms, TermsType.SERVICE);
        boolean hasPrivacyTerms = hasTermsOfType(requiredTerms, TermsType.PRIVACY);
        boolean hasMarketingTerms = hasTermsOfType(requiredTerms, TermsType.MARKETING);

        assertThat(hasServiceTerms).isTrue();
        assertThat(hasPrivacyTerms).isTrue();
        assertThat(hasMarketingTerms).isFalse(); // 마케팅은 필수 아님
    }

    @Test
    @DisplayName("약관 삭제 통합 테스트")
    void deleteById_ShouldRemoveTermsFromDatabase() {
        // when
        termsRepository.deleteById(serviceTerms.getId());

        // then
        assertThat(jpaTermsRepository.findById(serviceTerms.getId())).isEmpty();
    }

    // 헬퍼 메서드
    private TermsEntity saveTerms(String title, String content, TermsType type,
                                  String version, LocalDate effectiveDate, boolean required) {
        Terms terms = createTermsDomain(title, content, type, version, effectiveDate, required);
        return jpaTermsRepository.save(TermsEntity.from(terms));
    }

    private Terms createTermsDomain(String title, String content, TermsType type,
                                    String version, LocalDate effectiveDate, boolean required) {
        return Terms.create(
                Title.of(title),
                Content.of(content),
                type,
                Version.of(version),
                EffectiveDate.of(effectiveDate),
                Required.of(required)
        );
    }

    private boolean hasTermsOfType(List<Terms> termsList, TermsType type) {
        return termsList.stream()
                .anyMatch(terms -> terms.getType() == type);
    }
}