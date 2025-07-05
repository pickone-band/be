package com.pickone.domain.user.dto;

import com.pickone.global.common.enums.Genre;

public record UserPreferenceDto(
    Genre genre1,
    Genre genre2,
    Genre genre3,
    Genre genre4,
    Genre genre5,
    Genre genre6,
    Genre genre7,
    Genre genre8
) {}