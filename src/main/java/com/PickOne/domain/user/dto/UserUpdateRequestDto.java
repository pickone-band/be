package com.PickOne.domain.user.dto;

import com.PickOne.domain.user.model.entity.UserInstrumentEntity;
import com.PickOne.global.common.enums.Genre;
import com.PickOne.global.common.enums.Mbti;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserUpdateRequestDto(
        @Size(min = 2, max = 20)
        String nickname,
        String profileImageUrl,
        Boolean isPublic,
        Mbti mbti,
        List<Genre> genres,
        List<UserInstrumentDto> instruments
) {
    public List<UserInstrumentEntity> toInstrumentEntities() {
    if (instruments() == null) return List.of();
    return instruments().stream()
            .map(i -> UserInstrumentEntity.builder()
                    .instrument(i.instrument())
                    .proficiency(i.proficiency())
                    .build())
            .toList();
}

    public List<Genre> toGenres() {
        return genres() != null ? new ArrayList<>(genres()) : List.of();
    }
}