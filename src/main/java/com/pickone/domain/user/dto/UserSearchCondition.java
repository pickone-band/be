package com.pickone.domain.user.dto;

import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.domain.Role;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Instrument;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pickone.global.common.enums.Mbti;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "사용자 검색 조건")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSearchCondition {
    @Schema(description = "검색 키워드", example = "John")
    private String keyword;

    @Schema(description = "공개 사용자만 조회", example = "true")
    private Boolean onlyPublic;

    @Schema(description = "MBTI 필터", example = "ENFP")
    private Mbti mbti;

    @Schema(description = "악기 필터 목록", example = "[\"GUITAR\",\"DRUM\"]")
    private List<Instrument> instruments;

    @Schema(description = "장르 필터 목록", example = "[\"ROCK\",\"POP\"]")
    private List<Genre> genres;

    @Schema(description = "성별 필터", example = "FEMALE")
    private Gender gender;

    @Schema(description = "권한 필터", example = "USER")
    private Role role;

    @Schema(description = "최소 나이 필터", example = "18")
    private Integer minAge;

    @Schema(description = "최대 나이 필터", example = "30")
    private Integer maxAge;

    @Schema(description = "생년월일 시작", example = "1990-01-01")
    private LocalDate birthDateFrom;

    @Schema(description = "생년월일 종료", example = "2000-12-31")
    private LocalDate birthDateTo;
}
