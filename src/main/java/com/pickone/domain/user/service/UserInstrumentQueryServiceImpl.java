package com.pickone.domain.user.service;

import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import com.pickone.domain.user.repository.UserInstrumentJpaRepository;
import com.pickone.global.common.enums.Instrument;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInstrumentQueryServiceImpl implements UserInstrumentQueryService {

  private final UserInstrumentJpaRepository userInstrumentRepository;

  @Override
  public List<Instrument> getUserInstruments(Long userId) {
    return userInstrumentRepository.findByUserId(userId)
        .stream().map(UserInstrumentEntity::getInstrument).toList();
  }
}
