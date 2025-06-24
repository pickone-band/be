package com.pickone.domain.application.dto.request;

import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
import com.pickone.domain.recruitments.model.entity.Recruitment;
import com.pickone.domain.recruitments.model.entity.RecruitmentInstrument;

public class ApplicationInstrumentDto {
    private Instrument instrument;
    private Proficiency proficiency;

    public RecruitmentInstrument toEntity(Recruitment recruitment) {
        return RecruitmentInstrument.builder()
                .recruitment(recruitment)
                .instrument(instrument)
                .proficiency(proficiency)
                .build();
    }
}
