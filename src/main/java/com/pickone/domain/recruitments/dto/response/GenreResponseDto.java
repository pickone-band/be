package com.pickone.domain.recruitments.dto.response;

import com.pickone.global.common.enums.Genre;
import com.pickone.domain.recruitments.model.entity.RecruitmentGenre;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenreResponseDto {
    private List<Genre> genre;

    public static GenreResponseDto from(List<RecruitmentGenre> genreEntities) {
        List<Genre> genres = genreEntities.stream()
                .map(RecruitmentGenre::getGenre)
                .collect(Collectors.toList());

        return GenreResponseDto.builder()
                .genre(genres)
                .build();
    }
}
