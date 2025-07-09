package com.pickone.domain.userInstrument.service;

import com.pickone.global.common.enums.Instrument;
import java.util.List;

public interface UserInstrumentQueryService {
  List<Instrument> getUserInstruments(Long userId);
}