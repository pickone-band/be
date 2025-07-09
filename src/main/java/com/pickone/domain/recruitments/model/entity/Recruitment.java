package com.pickone.domain.recruitments.model.entity;

import com.pickone.domain.recruitments.dto.request.RecruitmentRequestDto;
import com.pickone.domain.user.entity.User;
import com.pickone.global.common.entity.BaseEntity;
import com.pickone.domain.recruitments.model.Status;
import com.pickone.domain.recruitments.model.Type;
import com.pickone.domain.recruitments.model.Visibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor()
public class Recruitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recruitment_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    private String title;
    private String description;
    private String region;
    private String thumbnail;
    private String snsLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void update(RecruitmentRequestDto dto) {
        if (dto.getTitle() != null) this.title = dto.getTitle();
        if (dto.getDescription() != null) this.description = dto.getDescription();
        if (dto.getRegion() != null) this.region = dto.getRegion();
        if (dto.getType() != null) this.type = dto.getType();
        if (dto.getStatus() != null) this.status = dto.getStatus();
        if (dto.getVisibility() != null) this.visibility = dto.getVisibility();
        if (dto.getThumbnail() != null) this.thumbnail = dto.getThumbnail();
        if (dto.getSnsLink() != null) this.snsLink = dto.getSnsLink();
    }

}
