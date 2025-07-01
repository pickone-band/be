package com.pickone.domain.user.dto;

import com.pickone.global.common.enums.Genre;

public record UserPreferenceDto(
    Genre primaryGenre,
    Genre secondaryGenre,
    Genre tertiaryGenre
) {}
