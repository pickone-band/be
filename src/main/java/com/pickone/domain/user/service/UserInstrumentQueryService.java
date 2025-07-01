package com.pickone.domain.user.service;

import com.pickone.global.common.enums.Instrument;
import java.util.List;

public interface UserInstrumentQueryService {
  List<Instrument> getUserInstruments(Long userId);
}