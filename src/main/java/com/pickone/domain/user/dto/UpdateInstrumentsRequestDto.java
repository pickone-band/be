package com.pickone.domain.user.dto;

import com.pickone.global.common.enums.Instrument;
import java.util.List;

public record UpdateInstrumentsRequestDto(
    List<Instrument> instruments  // 빈 리스트는 '악기 없음' 처리
) {}