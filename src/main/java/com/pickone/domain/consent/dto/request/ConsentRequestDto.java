package com.pickone.domain.consent.dto.request;

import com.pickone.domain.consent.model.entity.ConsentEntity;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.user.model.entity.UserEntity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsentRequestDto(@NotNull Long termId, @NotNull Boolean consented) {

  public ConsentEntity toEntity(UserEntity user, TermEntity term) {
    return new ConsentEntity(null, user, term, consented, LocalDateTime.now());
  }
}
