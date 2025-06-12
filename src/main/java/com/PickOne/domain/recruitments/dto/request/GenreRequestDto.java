package com.PickOne.domain.recruitments.dto.request;

import com.PickOne.global.common.enums.Genre;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenreRequestDto {
    private List<Genre> recruitmentGenres;
}
