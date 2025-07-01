package com.pickone.domain.consent.model.factory;

import com.pickone.domain.consent.model.entity.ConsentEntity;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.user.model.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConsentFactory {
  public ConsentEntity create(UserEntity user, TermEntity term, boolean consented) {
    return ConsentEntity.of(user, term, consented, LocalDateTime.now());
  }
}
