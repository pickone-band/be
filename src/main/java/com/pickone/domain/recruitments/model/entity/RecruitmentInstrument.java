package com.pickone.domain.recruitments.model.entity;

import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
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
public class RecruitmentInstrument {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false)
    private Recruitment recruitment;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrument")
    private Instrument instrument;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency")
    private Proficiency proficiency;

    public RecruitmentInstrument(Recruitment rec, Instrument inst, Proficiency prof) {
        this.recruitment = rec;
        this.instrument  = inst;
        this.proficiency  = prof;
    }
}