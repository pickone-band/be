package com.PickOne.domain.application.dto.request;

import com.PickOne.global.common.enums.Instrument;
import com.PickOne.global.common.enums.Proficiency;
import com.PickOne.domain.recruitments.model.entity.Recruitment;
import com.PickOne.domain.recruitments.model.entity.RecruitmentInstrument;

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
