package com.pickone.domain.user.dto;

import com.pickone.global.common.enums.Genre;
import java.util.List;

public record UpdatePreferenceRequestDto(
    List<Genre> genres // 최대 3개
) {}