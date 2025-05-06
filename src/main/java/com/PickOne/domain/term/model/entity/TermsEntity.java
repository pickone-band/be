package com.PickOne.domain.term.model.entity;

import com.PickOne.domain.term.model.domain.*;
import com.PickOne.domain.term.model.domain.Version;
import com.PickOne.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private LocalDateTime effectiveDate;

    @Column(nullable = false)
    private boolean required;

    // 생성자
    private TermsEntity(String title, String content, TermsType type, String version,
                        LocalDateTime effectiveDate, boolean required) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.version = version;
        this.effectiveDate = effectiveDate;
        this.required = required;
    }

    // 도메인 모델에서 엔티티로 변환
    public static TermsEntity from(Term term) {
        TermsEntity entity = new TermsEntity(
                term.getTitleValue(),
                term.getContentValue(),
                term.getType(),
                term.getVersionValue(),
                term.getEffectiveDateValue(),
                term.isRequiredValue()
        );

        if (!term.isNew()) {
            entity.id = term.getId();
        }

        return entity;
    }

    // 엔티티에서 도메인 모델로 변환
    public Term toDomain() {
        return Term.of(
                id,
                Title.of(title),
                Content.of(content),
                type,
                Version.of(version),
                EffectiveDate.of(effectiveDate),
                Required.of(required),
                getCreatedAt(),
                getUpdatedAt()
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
    public void updateEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    // 필수 여부 업데이트
    public void updateRequired(boolean required) {
        this.required = required;
    }

    // 현재 유효한 약관인지 확인
    public boolean isCurrentlyEffective() {
        return !LocalDateTime.now().isBefore(this.effectiveDate);
    }
}