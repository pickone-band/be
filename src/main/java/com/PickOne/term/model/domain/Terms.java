package com.PickOne.term.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")  // ID 기반 동등성 비교
public class Terms {

    private Long id;
    private Title title;
    private Content content;
    private TermsType type;
    private Version version;
    private EffectiveDate effectiveDate;
    private Required required;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;

    private Terms(Long id, Title title, Content content, TermsType type, Version version,
                  EffectiveDate effectiveDate, Required required, LocalDateTime createdAt,
                  LocalDateTime updatedAt, Long createdBy) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.version = version;
        this.effectiveDate = effectiveDate;
        this.required = required;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
    }

    // 신규 약관 생성 (ID 없음)
    public static Terms create(Title title, Content content, TermsType type, Version version,
                               EffectiveDate effectiveDate, Required required, Long createdBy) {
        validateTerms(title, content, type, version, effectiveDate, required);
        LocalDateTime now = LocalDateTime.now();
        return new Terms(null, title, content, type, version, effectiveDate, required,
                now, now, createdBy);
    }

    // 기존 약관 로드 (ID 있음)
    public static Terms of(Long id, Title title, Content content, TermsType type, Version version,
                           EffectiveDate effectiveDate, Required required, LocalDateTime createdAt,
                           LocalDateTime updatedAt, Long createdBy) {
        validateTerms(title, content, type, version, effectiveDate, required);
        return new Terms(id, title, content, type, version, effectiveDate, required,
                createdAt, updatedAt, createdBy);
    }

    // 약관 생성 시 추가 검증 로직
    private static void validateTerms(Title title, Content content, TermsType type,
                                      Version version, EffectiveDate effectiveDate, Required required) {
        if (title == null) {
            throw new IllegalArgumentException("제목은 null일 수 없습니다.");
        }
        if (content == null) {
            throw new IllegalArgumentException("내용은 null일 수 없습니다.");
        }
        if (type == null) {
            throw new IllegalArgumentException("유형은 null일 수 없습니다.");
        }
        if (version == null) {
            throw new IllegalArgumentException("버전은 null일 수 없습니다.");
        }
        if (effectiveDate == null) {
            throw new IllegalArgumentException("시행일은 null일 수 없습니다.");
        }
        if (required == null) {
            throw new IllegalArgumentException("필수 여부는 null일 수 없습니다.");
        }
    }

    // 내용 업데이트
    public Terms updateContent(Content newContent, Long updatedBy) {
        validateUpdater(updatedBy);
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    // 버전 업데이트
    public Terms updateVersion(Version newVersion, Long updatedBy) {
        validateUpdater(updatedBy);
        this.version = newVersion;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    // 시행일 업데이트
    public Terms updateEffectiveDate(EffectiveDate newEffectiveDate, Long updatedBy) {
        validateUpdater(updatedBy);
        this.effectiveDate = newEffectiveDate;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    // 필수 여부 업데이트
    public Terms updateRequired(Required newRequired, Long updatedBy) {
        validateUpdater(updatedBy);
        this.required = newRequired;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    // 업데이트 권한 검증
    private void validateUpdater(Long updatedBy) {
        if (updatedBy == null) {
            throw new IllegalArgumentException("업데이트를 수행하는 사용자 ID는 null일 수 없습니다.");
        }
    }

    // 편의 메서드
    public String getTitleValue() {
        return this.title.getValue();
    }

    public String getContentValue() {
        return this.content.getValue();
    }

    public String getTypeValue() {
        return this.type.getValue();
    }

    public String getTypeDisplayName() {
        return this.type.getDisplayName();
    }

    public String getVersionValue() {
        return this.version.getValue();
    }

    public LocalDate getEffectiveDateValue() {
        return this.effectiveDate.getValue();
    }

    public boolean isRequiredValue() {
        return this.required.isValue();
    }

    // 신규 약관 여부 확인
    public boolean isNew() {
        return this.id == null;
    }

    // 현재 유효한 약관인지 확인
    public boolean isCurrentlyEffective() {
        return this.effectiveDate.isEffectiveAt(LocalDate.now());
    }

    // 필수 동의 약관인지 확인
    public boolean isRequiredConsent() {
        return this.type.isRequiredConsent();
    }

    // 마케팅 약관인지 확인
    public boolean isMarketingConsent() {
        return this.type.isMarketingConsent();
    }
}