package com.PickOne.domain.consent.dto;

import com.PickOne.domain.consent.model.entity.ConsentEntity;
import com.PickOne.domain.term.model.entity.TermEntity;
import com.PickOne.domain.user.model.entity.UserEntity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsentRequestDto(
        @NotNull Long termsId,
        @NotNull Boolean consented
) {
    public ConsentEntity toEntity(UserEntity user, TermEntity term) {
        return new ConsentEntity(
                null,
                user,
                term,
                consented,
                LocalDateTime.now()
        );
    }
}
