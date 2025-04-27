package com.PickOne.term.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class TermsTest {

    private final String VALID_TITLE = "서비스 이용약관";
    private final String VALID_CONTENT = "약관 내용입니다.";
    private final TermsType VALID_TYPE = TermsType.SERVICE;
    private final String VALID_VERSION = "1.0.0";
    private final LocalDate VALID_EFFECTIVE_DATE = LocalDate.now();
    private final boolean VALID_REQUIRED = true;
    private final String VALID_UPDATER = "admin";

    @Test
    @DisplayName("유효한 파라미터로 Terms를 생성할 수 있다 - create 메서드")
    void createTermsWithValidParameters() {
        // given
        Title title = Title.of(VALID_TITLE);
        Content content = Content.of(VALID_CONTENT);
        Version version = Version.of(VALID_VERSION);
        EffectiveDate effectiveDate = EffectiveDate.of(VALID_EFFECTIVE_DATE);
        Required required = Required.of(VALID_REQUIRED);

        // when
        Terms terms = Terms.create(title, content, VALID_TYPE, version, effectiveDate, required);

        // then
        assertThat(terms).isNotNull();
        assertThat(terms.getId()).isNull();
        assertThat(terms.getTitleValue()).isEqualTo(VALID_TITLE);
        assertThat(terms.getContentValue()).isEqualTo(VALID_CONTENT);
        assertThat(terms.getType()).isEqualTo(VALID_TYPE);
        assertThat(terms.getVersionValue()).isEqualTo(VALID_VERSION);
        assertThat(terms.getEffectiveDateValue()).isEqualTo(VALID_EFFECTIVE_DATE);
        assertThat(terms.isRequiredValue()).isEqualTo(VALID_REQUIRED);
        assertThat(terms.getCreatedAt()).isNotNull();
        assertThat(terms.getUpdatedAt()).isNotNull();
        assertThat(terms.isNew()).isTrue();
    }

    @Test
    @DisplayName("유효한 파라미터로 Terms를 생성할 수 있다 - of 메서드")
    void createTermsWithValidParametersUsingOfMethod() {
        // given
        Long id = 1L;
        Title title = Title.of(VALID_TITLE);
        Content content = Content.of(VALID_CONTENT);
        Version version = Version.of(VALID_VERSION);
        EffectiveDate effectiveDate = EffectiveDate.of(VALID_EFFECTIVE_DATE);
        Required required = Required.of(VALID_REQUIRED);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        Terms terms = Terms.of(id, title, content, VALID_TYPE, version, effectiveDate, required, createdAt, updatedAt);

        // then
        assertThat(terms).isNotNull();
        assertThat(terms.getId()).isEqualTo(id);
        assertThat(terms.getTitleValue()).isEqualTo(VALID_TITLE);
        assertThat(terms.getContentValue()).isEqualTo(VALID_CONTENT);
        assertThat(terms.getType()).isEqualTo(VALID_TYPE);
        assertThat(terms.getVersionValue()).isEqualTo(VALID_VERSION);
        assertThat(terms.getEffectiveDateValue()).isEqualTo(VALID_EFFECTIVE_DATE);
        assertThat(terms.isRequiredValue()).isEqualTo(VALID_REQUIRED);
        assertThat(terms.getCreatedAt()).isEqualTo(createdAt);
        assertThat(terms.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(terms.isNew()).isFalse();
    }

    @Test
    @DisplayName("필수 파라미터가 null일 경우 예외가 발생한다 - title")
    void throwsExceptionWhenTitleIsNull() {
        // given
        Content content = Content.of(VALID_CONTENT);
        Version version = Version.of(VALID_VERSION);
        EffectiveDate effectiveDate = EffectiveDate.of(VALID_EFFECTIVE_DATE);
        Required required = Required.of(VALID_REQUIRED);

        // when & then
        assertThatThrownBy(() ->
                Terms.create(null, content, VALID_TYPE, version, effectiveDate, required))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("제목은 null일 수 없습니다");
    }

    @Test
    @DisplayName("필수 파라미터가 null일 경우 예외가 발생한다 - content")
    void throwsExceptionWhenContentIsNull() {
        // given
        Title title = Title.of(VALID_TITLE);
        Version version = Version.of(VALID_VERSION);
        EffectiveDate effectiveDate = EffectiveDate.of(VALID_EFFECTIVE_DATE);
        Required required = Required.of(VALID_REQUIRED);

        // when & then
        assertThatThrownBy(() ->
                Terms.create(title, null, VALID_TYPE, version, effectiveDate, required))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("내용은 null일 수 없습니다");
    }

    @Test
    @DisplayName("필수 파라미터가 null일 경우 예외가 발생한다 - type")
    void throwsExceptionWhenTypeIsNull() {
        // given
        Title title = Title.of(VALID_TITLE);
        Content content = Content.of(VALID_CONTENT);
        Version version = Version.of(VALID_VERSION);
        EffectiveDate effectiveDate = EffectiveDate.of(VALID_EFFECTIVE_DATE);
        Required required = Required.of(VALID_REQUIRED);

        // when & then
        assertThatThrownBy(() ->
                Terms.create(title, content, null, version, effectiveDate, required))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유형은 null일 수 없습니다");
    }

    @Test
    @DisplayName("약관 내용을 업데이트할 수 있다")
    void canUpdateContent() throws InterruptedException {
        // given
        Terms terms = createTestTerms();
        LocalDateTime beforeUpdate = terms.getUpdatedAt();

        // 시간 간격을 확실히 하기 위해 잠시 대기
        Thread.sleep(10);

        String newContentValue = "새로운 약관 내용입니다.";
        Content newContent = Content.of(newContentValue);

        // when
        Terms updatedTerms = terms.updateContent(newContent, VALID_UPDATER);

        // then
        assertThat(updatedTerms).isEqualTo(terms); // 동일 객체 참조 확인
        assertThat(updatedTerms.getContentValue()).isEqualTo(newContentValue);
        // 업데이트 시간이 이전과 같지 않은지만 확인
        assertThat(updatedTerms.getUpdatedAt()).isNotEqualTo(beforeUpdate);
    }

    @Test
    @DisplayName("약관 버전을 업데이트할 수 있다")
    void canUpdateVersion() throws InterruptedException {
        // given
        Terms terms = createTestTerms();
        LocalDateTime beforeUpdate = terms.getUpdatedAt();

        // 시간 간격을 확실히 하기 위해 잠시 대기
        Thread.sleep(10);

        String newVersionValue = "1.1.0";
        Version newVersion = Version.of(newVersionValue);

        // when
        Terms updatedTerms = terms.updateVersion(newVersion, VALID_UPDATER);

        // then
        assertThat(updatedTerms).isEqualTo(terms);
        assertThat(updatedTerms.getVersionValue()).isEqualTo(newVersionValue);
        // 업데이트 시간이 이전과 같지 않은지만 확인
        assertThat(updatedTerms.getUpdatedAt()).isNotEqualTo(beforeUpdate);
    }

    @Test
    @DisplayName("약관 시행일을 업데이트할 수 있다")
    void canUpdateEffectiveDate() throws InterruptedException {
        // given
        Terms terms = createTestTerms();
        LocalDateTime beforeUpdate = terms.getUpdatedAt();

        // 시간 간격을 확실히 하기 위해 잠시 대기
        Thread.sleep(10);

        LocalDate newDateValue = LocalDate.now().plusMonths(1);
        EffectiveDate newDate = EffectiveDate.of(newDateValue);

        // when
        Terms updatedTerms = terms.updateEffectiveDate(newDate, VALID_UPDATER);

        // then
        assertThat(updatedTerms).isEqualTo(terms);
        assertThat(updatedTerms.getEffectiveDateValue()).isEqualTo(newDateValue);
        // 업데이트 시간이 이전과 같지 않은지만 확인
        assertThat(updatedTerms.getUpdatedAt()).isNotEqualTo(beforeUpdate);
    }

    @Test
    @DisplayName("약관 필수 여부를 업데이트할 수 있다")
    void canUpdateRequiredFlag() throws InterruptedException {
        // given
        Terms terms = createTestTerms();
        LocalDateTime beforeUpdate = terms.getUpdatedAt();

        // 시간 간격을 확실히 하기 위해 잠시 대기
        Thread.sleep(10);

        boolean newRequiredValue = !terms.isRequiredValue();
        Required newRequired = Required.of(newRequiredValue);

        // when
        Terms updatedTerms = terms.updateRequired(newRequired, VALID_UPDATER);

        // then
        assertThat(updatedTerms).isEqualTo(terms);
        assertThat(updatedTerms.isRequiredValue()).isEqualTo(newRequiredValue);
        // 업데이트 시간이 이전과 같지 않은지만 확인
        assertThat(updatedTerms.getUpdatedAt()).isNotEqualTo(beforeUpdate);
    }

    @Test
    @DisplayName("업데이트 수행자가 null일 경우 예외가 발생한다")
    void throwsExceptionWhenUpdaterIsNull() {
        // given
        Terms terms = createTestTerms();
        Content newContent = Content.of("새로운 내용");

        // when & then
        assertThatThrownBy(() -> terms.updateContent(newContent, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("업데이트를 수행하는 사용자 ID는 null일 수 없습니다");
    }

    @Test
    @DisplayName("현재 날짜가 시행일 이후인 경우 유효한 약관으로 판단한다")
    void isEffectiveWhenCurrentDateIsAfterEffectiveDate() {
        // given
        LocalDate pastDate = LocalDate.now().minusDays(1);
        Terms terms = createTestTermsWithDate(pastDate);

        // when
        boolean isEffective = terms.isCurrentlyEffective();

        // then
        assertThat(isEffective).isTrue();
    }

    @Test
    @DisplayName("현재 날짜가 시행일 이전인 경우 유효하지 않은 약관으로 판단한다")
    void isNotEffectiveWhenCurrentDateIsBeforeEffectiveDate() {
        // given
        LocalDate futureDate = LocalDate.now().plusDays(1);
        Terms terms = createTestTermsWithDate(futureDate);

        // when
        boolean isEffective = terms.isCurrentlyEffective();

        // then
        assertThat(isEffective).isFalse();
    }

    @Test
    @DisplayName("서비스 약관과 개인정보 약관은 필수 동의 약관이다")
    void serviceAndPrivacyTermsAreRequiredConsent() {
        // given
        Terms serviceTerms = createTestTermsWithType(TermsType.SERVICE);
        Terms privacyTerms = createTestTermsWithType(TermsType.PRIVACY);
        Terms marketingTerms = createTestTermsWithType(TermsType.MARKETING);

        // when & then
        assertThat(serviceTerms.isRequiredConsent()).isTrue();
        assertThat(privacyTerms.isRequiredConsent()).isTrue();
        assertThat(marketingTerms.isRequiredConsent()).isFalse();
    }

    @Test
    @DisplayName("마케팅 약관은 마케팅 동의 약관으로 판단한다")
    void marketingTermsIsMarketingConsent() {
        // given
        Terms serviceTerms = createTestTermsWithType(TermsType.SERVICE);
        Terms marketingTerms = createTestTermsWithType(TermsType.MARKETING);

        // when & then
        assertThat(serviceTerms.isMarketingConsent()).isFalse();
        assertThat(marketingTerms.isMarketingConsent()).isTrue();
    }

    // 헬퍼 메서드
    private Terms createTestTerms() {
        Title title = Title.of(VALID_TITLE);
        Content content = Content.of(VALID_CONTENT);
        Version version = Version.of(VALID_VERSION);
        EffectiveDate effectiveDate = EffectiveDate.of(VALID_EFFECTIVE_DATE);
        Required required = Required.of(VALID_REQUIRED);

        return Terms.create(title, content, VALID_TYPE, version, effectiveDate, required);
    }

    private Terms createTestTermsWithDate(LocalDate date) {
        Title title = Title.of(VALID_TITLE);
        Content content = Content.of(VALID_CONTENT);
        Version version = Version.of(VALID_VERSION);
        EffectiveDate effectiveDate = EffectiveDate.of(date);
        Required required = Required.of(VALID_REQUIRED);

        return Terms.create(title, content, VALID_TYPE, version, effectiveDate, required);
    }

    private Terms createTestTermsWithType(TermsType type) {
        Title title = Title.of(VALID_TITLE);
        Content content = Content.of(VALID_CONTENT);
        Version version = Version.of(VALID_VERSION);
        EffectiveDate effectiveDate = EffectiveDate.of(VALID_EFFECTIVE_DATE);
        Required required = Required.of(VALID_REQUIRED);

        return Terms.create(title, content, type, version, effectiveDate, required);
    }
}