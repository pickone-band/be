package com.pickone.domain.user.dto;

import com.pickone.global.common.enums.Genre;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "선호 장르 정보")
public record UserPreferenceResponse(
    @Schema(description = "장르1", example = "ROCK") Genre genre1,
    @Schema(description = "장르2", example = "POP") Genre genre2,
    @Schema(description = "장르3", example = "JAZZ") Genre genre3,
    @Schema(description = "장르4", example = "CLASSICAL") Genre genre4,
    @Schema(description = "장르5", example = "HIPHOP") Genre genre5,
    @Schema(description = "장르6", example = "BLUES") Genre genre6,
    @Schema(description = "장르7", example = "COUNTRY") Genre genre7,
    @Schema(description = "장르8", example = "LATIN") Genre genre8
) {}
