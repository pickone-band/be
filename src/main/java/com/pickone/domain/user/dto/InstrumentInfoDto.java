package com.pickone.domain.user.dto;

import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;

public record InstrumentInfoDto(
    Instrument type,
    Proficiency level
) {}
