package com.PickOne.domain.recruitments.dto.response;

import com.PickOne.domain.recruitments.model.Instrument;
import com.PickOne.domain.recruitments.model.Proficiency;
import com.PickOne.domain.recruitments.model.entity.RecruitmentInstrument;
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