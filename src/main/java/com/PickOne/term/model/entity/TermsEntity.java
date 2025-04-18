package com.PickOne.term.model.entity;

import com.PickOne.common.entity.BaseEntity;
import com.PickOne.term.model.domain.*;
import com.PickOne.term.model.domain.Version;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TermsType type;

    @Column(nullable = false, length = 20)
    private String version;

    @Column(nullable = false, name = "effective_date")
    private LocalDate effectiveDate;

    @Column(nullable = false)
    private boolean required;

    // 생성자
    private TermsEntity(String title, String content, TermsType type, String version,
                        LocalDate effectiveDate, boolean required) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.version = version;
        this.effectiveDate = effectiveDate;
        this.required = required;
    }

    // 도메인 모델에서 엔티티로 변환
    public static TermsEntity from(Terms terms) {
        TermsEntity entity = new TermsEntity(
                terms.getTitleValue(),
                terms.getContentValue(),
                terms.getType(),
                terms.getVersionValue(),
                terms.getEffectiveDateValue(),
                terms.isRequiredValue()
        );

        if (!terms.isNew()) {
            entity.id = terms.getId();
        }

        return entity;
    }

    // 엔티티에서 도메인 모델로 변환
    public Terms toDomain() {
        return Terms.of(
                id,
                Title.of(title),
                Content.of(content),
                type,
                Version.of(version),
                EffectiveDate.of(effectiveDate),
                Required.of(required),
                getCreatedAt(),
                getUpdatedAt(),
                Long.valueOf(getCreatedBy())
        );
    }

    // 내용 업데이트
    public void updateContent(String content) {
        this.content = content;
    }

    // 버전 업데이트
    public void updateVersion(String version) {
        this.version = version;
    }

    // 시행일 업데이트
    public void updateEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    // 필수 여부 업데이트
    public void updateRequired(boolean required) {
        this.required = required;
    }

    // 현재 유효한 약관인지 확인
    public boolean isCurrentlyEffective() {
        return !LocalDate.now().isBefore(this.effectiveDate);
    }
}