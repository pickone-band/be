package com.PickOne.domain.user.dto;

import com.PickOne.domain.user.model.domain.Gender;
import com.PickOne.domain.user.model.domain.Role;
import com.PickOne.global.common.enums.Genre;
import com.PickOne.global.common.enums.Instrument;
import com.PickOne.global.common.enums.Mbti;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserSearchConditionDto(
        String keyword,
        Boolean onlyPublic,
        Mbti mbti,
        List<Instrument> instruments,
        List<Genre> genres,
        Gender gender,
        Role role,
        Integer minAge,
        Integer maxAge,
        LocalDate birthDateFrom,
        LocalDate birthDateTo
) {}