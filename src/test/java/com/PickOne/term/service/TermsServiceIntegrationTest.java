package com.PickOne.term.service;

import com.PickOne.term.model.domain.Terms;
import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.domain.Version;
import com.PickOne.term.model.entity.TermsEntity;
import com.PickOne.term.repository.JpaTermsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class TermsServiceIntegrationTest {

    @Autowired
    private TermsService termsService;

    @Autowired
    private JpaTermsRepository jpaTermsRepository;

    private final String TEST_USER = "test-user";
    private final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(TEST_USER, null, "ROLE_USER"));
        jpaTermsRepository.deleteAll();
    }

    @Test
    @DisplayName("약관 생성 서비스 통합 테스트")
    void createTerms_ShouldCreateAndPersistTerms() {
        // given
        String title = "서비스 이용약관";
        String content = "서비스 약관 내용";
        TermsType type = TermsType.SERVICE;
        Version version = Version.of("1.0.0");
        LocalDate effectiveDate = TODAY;
        boolean required = true;

        // when
        Terms createdTerms = termsService.createTerms(title, content, type, version, effectiveDate, required);

        // then
        assertThat(createdTerms).isNotNull();
        assertThat(createdTerms.getId()).isNotNull();
        assertThat(createdTerms.getTitleValue()).isEqualTo(title);
        assertThat(createdTerms.getContentValue()).isEqualTo(content);
        assertThat(createdTerms.getType()).isEqualTo(type);
        assertThat(createdTerms.getVersion()).isEqualTo(version);
        assertThat(createdTerms.getEffectiveDateValue()).isEqualTo(effectiveDate);
        assertThat(createdTerms.isRequiredValue()).isEqualTo(required);

        // DB 저장 확인
        TermsEntity savedEntity = jpaTermsRepository.findById(createdTerms.getId())
                .orElseThrow();
        assertThat(savedEntity.getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("중복 버전 약관 생성 시 예외 발생 서비스 통합 테스트")
    void createTerms_WithDuplicateVersion_ShouldThrowException() {
        // given
        termsService.createTerms("서비스 이용약관", "내용1", TermsType.SERVICE, Version.of("1.0.0"), TODAY, true);

        // when & then
        assertThatThrownBy(() ->
                termsService.createTerms("서비스 이용약관 수정", "내용2", TermsType.SERVICE, Version.of("1.0.0"), TODAY, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 약관 유형");
    }

    @Test
    @DisplayName("ID로 약관 조회 서비스 통합 테스트")
    void getTermsById_ShouldReturnCorrectTerms() {
        // given
        Terms createdTerms = termsService.createTerms(
                "서비스 이용약관", "내용", TermsType.SERVICE, Version.of("1.0.0"), TODAY, true);

        // when
        Terms foundTerms = termsService.getTermsById(createdTerms.getId());

        // then
        assertThat(foundTerms).isNotNull();
        assertThat(foundTerms.getId()).isEqualTo(createdTerms.getId());
        assertThat(foundTerms.getTitleValue()).isEqualTo("서비스 이용약관");
    }

    @Test
    @DisplayName("존재하지 않는 약관 조회 시 예외 발생 서비스 통합 테스트")
    void getTermsById_WithNonExistingId_ShouldThrowException() {
        // when & then
        assertThatThrownBy(() -> termsService.getTermsById(999L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("존재하지 않습니다");
    }

    @Test
    @DisplayName("약관 내용 업데이트 서비스 통합 테스트")
    void updateTermsContent_ShouldUpdateContentField() {
        // given
        Terms createdTerms = termsService.createTerms(
                "서비스 이용약관", "초기 내용", TermsType.SERVICE, Version.of("1.0.0"), TODAY, true);

        String newContent = "업데이트된 내용";

        // when
        Terms updatedTerms = termsService.updateTermsContent(createdTerms.getId(), newContent);

        // then
        assertThat(updatedTerms.getContentValue()).isEqualTo(newContent);

        // DB 반영 확인
        TermsEntity entity = jpaTermsRepository.findById(createdTerms.getId()).orElseThrow();
        assertThat(entity.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("약관 버전 업데이트 서비스 통합 테스트")
    void updateTermsVersion_ShouldUpdateVersionField() {
        // given
        Terms createdTerms = termsService.createTerms(
                "서비스 이용약관", "내용", TermsType.SERVICE, Version.of("1.0.0"), TODAY, true);

        String newVersion = "1.1.0";

        // when
        Terms updatedTerms = termsService.updateTermsVersion(createdTerms.getId(), newVersion);

        // then
        assertThat(updatedTerms.getVersionValue()).isEqualTo(newVersion);

        // DB 반영 확인
        TermsEntity entity = jpaTermsRepository.findById(createdTerms.getId()).orElseThrow();
        assertThat(entity.getVersion()).isEqualTo(newVersion);
    }

    @Test
    @DisplayName("약관 시행일 업데이트 서비스 통합 테스트")
    void updateTermsEffectiveDate_ShouldUpdateEffectiveDateField() {
        // given
        Terms createdTerms = termsService.createTerms(
                "서비스 이용약관", "내용", TermsType.SERVICE, Version.of("1.0.0"), TODAY, true);

        LocalDate newDate = TODAY.plusMonths(1);

        // when
        Terms updatedTerms = termsService.updateTermsEffectiveDate(createdTerms.getId(), newDate);

        // then
        assertThat(updatedTerms.getEffectiveDateValue()).isEqualTo(newDate);

        // DB 반영 확인
        TermsEntity entity = jpaTermsRepository.findById(createdTerms.getId()).orElseThrow();
        assertThat(entity.getEffectiveDate()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("약관 필수 여부 업데이트 서비스 통합 테스트")
    void updateTermsRequired_ShouldUpdateRequiredField() {
        // given
        Terms createdTerms = termsService.createTerms(
                "서비스 이용약관", "내용", TermsType.SERVICE, Version.of("1.0.0"), TODAY, true);

        boolean newRequired = false;

        // when
        Terms updatedTerms = termsService.updateTermsRequired(createdTerms.getId(), newRequired);

        // then
        assertThat(updatedTerms.isRequiredValue()).isEqualTo(newRequired);

        // DB 반영 확인
        TermsEntity entity = jpaTermsRepository.findById(createdTerms.getId()).orElseThrow();
        assertThat(entity.isRequired()).isEqualTo(newRequired);
    }

    @Test
    @DisplayName("유형별 최신 약관 조회 서비스 통합 테스트")
    void getLatestTermsByType_ShouldReturnMostRecentVersion() {
        // given
        termsService.createTerms(
                "서비스 이용약관 V1", "내용1", TermsType.SERVICE, Version.of("1.0.0"), TODAY, true);

        termsService.createTerms(
                "서비스 이용약관 V2", "내용2", TermsType.SERVICE, Version.of("1.1.0"), TODAY, true);

        // when
        Terms latestTerms = termsService.getLatestTermsByType(TermsType.SERVICE);

        // then
        assertThat(latestTerms).isNotNull();
        assertThat(latestTerms.getTitleValue()).isEqualTo("서비스 이용약관 V2");
        assertThat(latestTerms.getVersionValue()).isEqualTo("1.1.0");
    }

    @Test
    @DisplayName("현재 유효한 약관 조회 서비스 통합 테스트")
    void getCurrentlyEffectiveTermsByType_ShouldReturnCurrentValidTerms() {
        // given
        termsService.createTerms(
                "서비스 이용약관 현재", "현재 약관", TermsType.SERVICE, Version.of("1.0.0"), TODAY.minusDays(1), true);

        termsService.createTerms(
                "서비스 이용약관 미래", "미래 약관", TermsType.SERVICE, Version.of("1.1.0"), TODAY.plusDays(10), true);

        // when
        Terms effectiveTerms = termsService.getCurrentlyEffectiveTermsByType(TermsType.SERVICE);

        // then
        assertThat(effectiveTerms).isNotNull();
        assertThat(effectiveTerms.getTitleValue()).isEqualTo("서비스 이용약관 현재");
        assertThat(effectiveTerms.getVersionValue()).isEqualTo("1.0.0");
    }

    @Test
    @DisplayName("필수 약관 목록 조회 서비스 통합 테스트")
    void getAllRequiredTerms_ShouldReturnOnlyRequiredTerms() {
        // given
        createThreeTermsTypes();

        // when
        List<Terms> requiredTerms = termsService.getAllRequiredTerms();

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
    @DisplayName("향후 시행 예정 약관 조회 서비스 통합 테스트")
    void getUpcomingTerms_ShouldReturnFutureTermsInOrder() {
        // given
        termsService.createTerms(
                "서비스 이용약관 현재", "현재 약관", TermsType.SERVICE, Version.of("1.0.0"), TODAY.minusDays(1), true);

        termsService.createTerms(
                "서비스 이용약관 미래", "미래 약관", TermsType.SERVICE, Version.of("1.1.0"), TODAY.plusDays(10), true);

        termsService.createTerms(
                "개인정보 처리방침 미래", "미래 개인정보 약관", TermsType.PRIVACY, Version.of("1.1.0"), TODAY.plusDays(5), true);

        // when
        List<Terms> upcomingTerms = termsService.getUpcomingTerms();

        // then
        assertThat(upcomingTerms).hasSize(2); // 미래 버전 2개

        // 날짜순으로 정렬되는지 확인
        assertThat(upcomingTerms.get(0).getTitleValue()).isEqualTo("개인정보 처리방침 미래"); // 5일 후
        assertThat(upcomingTerms.get(1).getTitleValue()).isEqualTo("서비스 이용약관 미래"); // 10일 후
    }

    @Test
    @DisplayName("약관 삭제 서비스 통합 테스트")
    void deleteTerms_ShouldRemoveTermsFromDatabase() {
        // given
        Terms createdTerms = termsService.createTerms(
                "서비스 이용약관", "내용", TermsType.SERVICE, Version.of("1.0.0"), TODAY, true);

        Long termsId = createdTerms.getId();

        // when
        termsService.deleteTerms(termsId);

        // then
        assertThat(jpaTermsRepository.existsById(termsId)).isFalse();
        assertThatThrownBy(() -> termsService.getTermsById(termsId))
                .isInstanceOf(NoSuchElementException.class);
    }

    // 헬퍼 메서드
    private void createThreeTermsTypes() {
        termsService.createTerms(
                "서비스 이용약관", "서비스 약관", TermsType.SERVICE, Version.of("1.0.0"), TODAY, true);

        termsService.createTerms(
                "개인정보 처리방침", "개인정보 약관", TermsType.PRIVACY, Version.of("1.0.0"), TODAY, true);

        termsService.createTerms(
                "마케팅 수신 동의", "마케팅 약관", TermsType.MARKETING, Version.of("1.0.0"), TODAY, false);
    }

    private boolean hasTermsOfType(List<Terms> termsList, TermsType type) {
        return termsList.stream()
                .anyMatch(terms -> terms.getType() == type);
    }
}