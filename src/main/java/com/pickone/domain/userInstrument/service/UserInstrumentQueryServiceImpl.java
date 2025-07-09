package com.pickone.domain.userInstrument.service;

import com.pickone.domain.userInstrument.entity.UserInstrument;
import com.pickone.domain.userInstrument.repository.UserInstrumentJpaRepository;
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
        .stream().map(UserInstrument::getInstrument).toList();
  }
}
