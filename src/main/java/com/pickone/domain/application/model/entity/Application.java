package com.pickone.domain.application.model.entity;


import com.pickone.domain.application.dto.request.ApplicationRequestDto;
import com.pickone.domain.application.model.ApplicationStatus;
import com.pickone.domain.user.entity.User;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Mbti;
import com.pickone.global.common.enums.Proficiency;
import com.pickone.domain.recruitments.model.entity.Recruitment;
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
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
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
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="application_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    private String message;
    private String portfolioUrl;
    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Mbti mbti;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Instrument instrument;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Proficiency proficiency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id")
    private Recruitment recruitment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ApplicationStatus.PENDING; // 기본 상태
        }
    }

    public void update(ApplicationRequestDto applicationRequestDto) {
        this.message = applicationRequestDto.getMessage();
        this.portfolioUrl = applicationRequestDto.getPortfolioUrl();
        this.thumbnail = applicationRequestDto.getThumbnail();
        this.mbti = applicationRequestDto.getMbti();
        this.instrument = applicationRequestDto.getInstrument();
        this.proficiency = applicationRequestDto.getProficiency();
    }

    public void changeStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
    }


}
