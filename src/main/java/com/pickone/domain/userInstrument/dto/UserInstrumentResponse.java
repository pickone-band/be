package com.pickone.domain.userInstrument.dto;

import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "악기 정보")
public record UserInstrumentResponse(
    @Schema(description = "ID", example = "10") Long id,
    @Schema(description = "악기명", example = "GUITAR") Instrument instrument,
    @Schema(description = "숙련도", example = "INTERMEDIATE") Proficiency proficiency
) {}