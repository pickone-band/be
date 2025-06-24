package com.pickone.domain.recruitments.dto.request;

import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
import com.pickone.domain.recruitments.model.entity.Recruitment;
import com.pickone.domain.recruitments.model.entity.RecruitmentInstrument;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InstrumentProficiencyDto {
    private List<InstrumentDetail> instrumentDetails;

    @Getter
    @Builder
    public static class InstrumentDetail {
        private Instrument instrument;
        private Proficiency proficiency;
    }

    public List<RecruitmentInstrument> toEntityList(Recruitment recruitment) {
        return this.instrumentDetails.stream()
                .map(detail -> {
                    RecruitmentInstrument recruitmentInstrument = RecruitmentInstrument.builder()
                            .recruitment(recruitment)
                            .instrument(detail.getInstrument())
                            .proficiency(detail.getProficiency())
                            .build();
                    return recruitmentInstrument;
                })
                .collect(Collectors.toList());
    }
}
