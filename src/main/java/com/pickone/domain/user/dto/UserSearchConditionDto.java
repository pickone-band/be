package com.pickone.domain.user.dto;

import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.domain.Role;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Mbti;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchConditionDto {
    private String keyword;
    private Boolean onlyPublic;
    private Mbti mbti;
    private List<Instrument> instruments;
    private List<Genre> genres;
    private Gender gender;
    private Role role;
    private Integer minAge;
    private Integer maxAge;
    private LocalDate birthDateFrom;
    private LocalDate birthDateTo;
}
