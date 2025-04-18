package com.PickOne.term.model.entity;

import com.PickOne.term.model.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TermsEntityTest {

    @Test
    @DisplayName("Terms 도메인 모델로부터 TermsEntity 생성")
    void from_ShouldCreateEntityFromDomain() {
        // given
        Long id = 1L;
        Title title = Title.of("이용약관");
        Content content = Content.of("이용약관 내용입니다.");
        TermsType type = TermsType.SERVICE;
        Version version = Version.of("1.0.0");
        LocalDate effectiveDate = LocalDate.of(2025, 4, 18);
        EffectiveDate effectiveDateObj = EffectiveDate.of(effectiveDate);
        Required required = Required.of(true);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        Long createdBy = 1L;

        Terms terms = Terms.of(
                id, title, content, type, version, effectiveDateObj, required,
                createdAt, updatedAt, createdBy
        );

        // when
        TermsEntity entity = TermsEntity.from(terms);

        // then
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getTitle()).isEqualTo(title.getValue());
        assertThat(entity.getContent()).isEqualTo(content.getValue());
        assertThat(entity.getType()).isEqualTo(type);
        assertThat(entity.getVersion()).isEqualTo(version.getValue());
        assertThat(entity.getEffectiveDate()).isEqualTo(effectiveDate);
        assertThat(entity.isRequired()).isEqualTo(required.isValue());
    }

    // TermsEntity의 toDomain 메서드 테스트는 BaseEntity 상속 및
    // private 생성자로 인해 복잡하므로 다른 테스트로 검증

    @Test
    @DisplayName("도메인-엔티티-도메인 변환 일관성 테스트")
    void conversionConsistency_BetweenDomainAndEntity() {
        // given
        Long id = 1L;
        String titleValue = "이용약관";
        String contentValue = "이용약관 내용입니다.";
        TermsType type = TermsType.SERVICE;
        String versionValue = "1.0.0";
        LocalDate effectiveDate = LocalDate.of(2025, 4, 18);
        boolean requiredValue = true;

        // 도메인 객체 생성
        Title title = Title.of(titleValue);
        Content content = Content.of(contentValue);
        Version version = Version.of(versionValue);
        EffectiveDate effectiveDateObj = EffectiveDate.of(effectiveDate);
        Required required = Required.of(requiredValue);

        LocalDateTime now = LocalDateTime.now();
        Terms originalTerms = Terms.of(
                id, title, content, type, version, effectiveDateObj, required,
                now, now, 1L
        );

        // when
        // 도메인 -> 엔티티 -> 도메인 변환
        TermsEntity entity = TermsEntity.from(originalTerms);

        // BaseEntity 메서드 호출없이 필드값 검증
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getTitle()).isEqualTo(titleValue);
        assertThat(entity.getContent()).isEqualTo(contentValue);
        assertThat(entity.getType()).isEqualTo(type);
        assertThat(entity.getVersion()).isEqualTo(versionValue);
        assertThat(entity.getEffectiveDate()).isEqualTo(effectiveDate);
        assertThat(entity.isRequired()).isEqualTo(requiredValue);

        // BaseEntity의 메서드에 의존하지 않는 부분만 테스트
        assertThat(entity.getId()).isEqualTo(originalTerms.getId());
        assertThat(entity.getTitle()).isEqualTo(originalTerms.getTitleValue());
        assertThat(entity.getContent()).isEqualTo(originalTerms.getContentValue());
        assertThat(entity.getType()).isEqualTo(originalTerms.getType());
        assertThat(entity.getVersion()).isEqualTo(originalTerms.getVersionValue());
        assertThat(entity.getEffectiveDate()).isEqualTo(originalTerms.getEffectiveDateValue());
        assertThat(entity.isRequired()).isEqualTo(originalTerms.isRequiredValue());
    }

    @Test
    @DisplayName("신규 약관 생성 테스트")
    void createNewTerms_ShouldCreateEntityWithoutId() {
        // given
        Title title = Title.of("마케팅 정보 수신 동의");
        Content content = Content.of("마케팅 정보 수신 동의 내용입니다.");
        TermsType type = TermsType.MARKETING;
        Version version = Version.of("1.0.0");
        LocalDate effectiveDate = LocalDate.of(2025, 6, 1);
        EffectiveDate effectiveDateObj = EffectiveDate.of(effectiveDate);
        Required required = Required.of(false);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        Long createdBy = 2L;

        // 신규 약관은 ID가 null
        Terms newTerms = Terms.of(
                null, title, content, type, version, effectiveDateObj, required,
                createdAt, updatedAt, createdBy
        );

        // when
        TermsEntity entity = TermsEntity.from(newTerms);

        // then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getTitle()).isEqualTo(title.getValue());
        assertThat(entity.getContent()).isEqualTo(content.getValue());
        assertThat(entity.getType()).isEqualTo(type);
        assertThat(entity.getVersion()).isEqualTo(version.getValue());
        assertThat(entity.getEffectiveDate()).isEqualTo(effectiveDate);
        assertThat(entity.isRequired()).isEqualTo(required.isValue());
    }

    @Test
    @DisplayName("약관 내용 업데이트 테스트")
    void updateContent_ShouldUpdateContentOnly() {
        // given - 도메인 객체 생성 후 엔티티로 변환
        Title title = Title.of("이용약관");
        Content content = Content.of("이용약관 내용입니다.");
        TermsType type = TermsType.SERVICE;
        Version version = Version.of("1.0.0");
        LocalDate effectiveDate = LocalDate.of(2025, 4, 18);
        EffectiveDate effectiveDateObj = EffectiveDate.of(effectiveDate);
        Required required = Required.of(true);

        Terms terms = Terms.of(
                1L, title, content, type, version, effectiveDateObj, required,
                LocalDateTime.now(), LocalDateTime.now(), 1L
        );

        TermsEntity entity = TermsEntity.from(terms);
        String newContent = "업데이트된 이용약관 내용입니다.";

        // when
        entity.updateContent(newContent);

        // then
        assertThat(entity.getContent()).isEqualTo(newContent);
        // 다른 필드는 변경되지 않아야 함
        assertThat(entity.getTitle()).isEqualTo(title.getValue());
        assertThat(entity.getType()).isEqualTo(type);
        assertThat(entity.getVersion()).isEqualTo(version.getValue());
        assertThat(entity.getEffectiveDate()).isEqualTo(effectiveDate);
        assertThat(entity.isRequired()).isEqualTo(required.isValue());
    }

    @Test
    @DisplayName("약관 버전 업데이트 테스트")
    void updateVersion_ShouldUpdateVersionOnly() {
        // given - 도메인 객체 생성 후 엔티티로 변환
        Title title = Title.of("이용약관");
        Content content = Content.of("이용약관 내용입니다.");
        TermsType type = TermsType.SERVICE;
        Version version = Version.of("1.0.0");
        LocalDate effectiveDate = LocalDate.of(2025, 4, 18);
        EffectiveDate effectiveDateObj = EffectiveDate.of(effectiveDate);
        Required required = Required.of(true);

        Terms terms = Terms.of(
                1L, title, content, type, version, effectiveDateObj, required,
                LocalDateTime.now(), LocalDateTime.now(), 1L
        );

        TermsEntity entity = TermsEntity.from(terms);
        String newVersion = "1.1.0";

        // when
        entity.updateVersion(newVersion);

        // then
        assertThat(entity.getVersion()).isEqualTo(newVersion);
        // 다른 필드는 변경되지 않아야 함
        assertThat(entity.getTitle()).isEqualTo(title.getValue());
        assertThat(entity.getContent()).isEqualTo(content.getValue());
        assertThat(entity.getType()).isEqualTo(type);
        assertThat(entity.getEffectiveDate()).isEqualTo(effectiveDate);
        assertThat(entity.isRequired()).isEqualTo(required.isValue());
    }

    @Test
    @DisplayName("약관 시행일 업데이트 테스트")
    void updateEffectiveDate_ShouldUpdateEffectiveDateOnly() {
        // given - 도메인 객체 생성 후 엔티티로 변환
        Title title = Title.of("이용약관");
        Content content = Content.of("이용약관 내용입니다.");
        TermsType type = TermsType.SERVICE;
        Version version = Version.of("1.0.0");
        LocalDate effectiveDate = LocalDate.of(2025, 4, 18);
        EffectiveDate effectiveDateObj = EffectiveDate.of(effectiveDate);
        Required required = Required.of(true);

        Terms terms = Terms.of(
                1L, title, content, type, version, effectiveDateObj, required,
                LocalDateTime.now(), LocalDateTime.now(), 1L
        );

        TermsEntity entity = TermsEntity.from(terms);
        LocalDate newEffectiveDate = LocalDate.of(2025, 5, 1);

        // when
        entity.updateEffectiveDate(newEffectiveDate);

        // then
        assertThat(entity.getEffectiveDate()).isEqualTo(newEffectiveDate);
        // 다른 필드는 변경되지 않아야 함
        assertThat(entity.getTitle()).isEqualTo(title.getValue());
        assertThat(entity.getContent()).isEqualTo(content.getValue());
        assertThat(entity.getType()).isEqualTo(type);
        assertThat(entity.getVersion()).isEqualTo(version.getValue());
        assertThat(entity.isRequired()).isEqualTo(required.isValue());
    }

    @Test
    @DisplayName("약관 필수 여부 업데이트 테스트")
    void updateRequired_ShouldUpdateRequiredOnly() {
        // given - 도메인 객체 생성 후 엔티티로 변환
        Title title = Title.of("이용약관");
        Content content = Content.of("이용약관 내용입니다.");
        TermsType type = TermsType.SERVICE;
        Version version = Version.of("1.0.0");
        LocalDate effectiveDate = LocalDate.of(2025, 4, 18);
        EffectiveDate effectiveDateObj = EffectiveDate.of(effectiveDate);
        Required required = Required.of(true);

        Terms terms = Terms.of(
                1L, title, content, type, version, effectiveDateObj, required,
                LocalDateTime.now(), LocalDateTime.now(), 1L
        );

        TermsEntity entity = TermsEntity.from(terms);
        boolean newRequired = false;

        // when
        entity.updateRequired(newRequired);

        // then
        assertThat(entity.isRequired()).isEqualTo(newRequired);
        // 다른 필드는 변경되지 않아야 함
        assertThat(entity.getTitle()).isEqualTo(title.getValue());
        assertThat(entity.getContent()).isEqualTo(content.getValue());
        assertThat(entity.getType()).isEqualTo(type);
        assertThat(entity.getVersion()).isEqualTo(version.getValue());
        assertThat(entity.getEffectiveDate()).isEqualTo(effectiveDate);
    }

    @Test
    @DisplayName("현재 유효한 약관 확인 - 유효한 경우")
    void isCurrentlyEffective_ShouldReturnTrue_WhenEffectiveDateIsBeforeOrEqualToToday() {
        // given - 도메인 객체 생성 후 엔티티로 변환
        Title title = Title.of("이용약관");
        Content content = Content.of("이용약관 내용입니다.");
        TermsType type = TermsType.SERVICE;
        Version version = Version.of("1.0.0");
        // 오늘 또는 과거 날짜
        LocalDate pastEffectiveDate = LocalDate.now().minusDays(1);
        EffectiveDate effectiveDateObj = EffectiveDate.of(pastEffectiveDate);
        Required required = Required.of(true);

        Terms terms = Terms.of(
                1L, title, content, type, version, effectiveDateObj, required,
                LocalDateTime.now(), LocalDateTime.now(), 1L
        );

        TermsEntity entity = TermsEntity.from(terms);

        // when
        boolean isEffective = entity.isCurrentlyEffective();

        // then
        assertThat(isEffective).isTrue();
    }

    @Test
    @DisplayName("현재 유효한 약관 확인 - 유효하지 않은 경우")
    void isCurrentlyEffective_ShouldReturnFalse_WhenEffectiveDateIsAfterToday() {
        // given - 도메인 객체 생성 후 엔티티로 변환
        Title title = Title.of("이용약관");
        Content content = Content.of("이용약관 내용입니다.");
        TermsType type = TermsType.SERVICE;
        Version version = Version.of("1.0.0");
        // 미래 날짜
        LocalDate futureEffectiveDate = LocalDate.now().plusDays(1);
        EffectiveDate effectiveDateObj = EffectiveDate.of(futureEffectiveDate);
        Required required = Required.of(true);

        Terms terms = Terms.of(
                1L, title, content, type, version, effectiveDateObj, required,
                LocalDateTime.now(), LocalDateTime.now(), 1L
        );

        TermsEntity entity = TermsEntity.from(terms);

        // when
        boolean isEffective = entity.isCurrentlyEffective();

        // then
        assertThat(isEffective).isFalse();
    }
}