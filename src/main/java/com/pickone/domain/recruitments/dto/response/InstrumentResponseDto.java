package com.pickone.domain.recruitments.dto.response;

import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
import com.pickone.domain.recruitments.model.entity.RecruitmentInstrument;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstrumentResponseDto {
    private Instrument instrument;
    private Proficiency proficiency;

    public static InstrumentResponseDto from(RecruitmentInstrument ri) {
        return InstrumentResponseDto.builder()
                .instrument(ri.getInstrument())
                .proficiency(ri.getProficiency())
                .build();
    }
}