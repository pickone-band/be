package com.pickone.domain.userInstrument.dto;

import com.pickone.global.common.enums.Instrument;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "사용자 악기 수정 요청")
public record UpdateInstrumentsRequest(

    @Schema(
        description = "사용자가 연주 가능한 악기 목록 (빈 리스트는 '악기 없음'으로 처리)",
        example = "[\"GUITAR\", \"PIANO\"]"
    )
    List<Instrument> instruments
) {}