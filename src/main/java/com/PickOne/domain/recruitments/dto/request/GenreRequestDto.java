package com.PickOne.domain.recruitments.dto.request;

import com.PickOne.domain.recruitments.model.Genre;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenreRequestDto {
    private List<Genre> recruitmentGenres;
}
