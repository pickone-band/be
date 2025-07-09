package com.pickone.domain.userInstrument.service;

import com.pickone.global.common.enums.Instrument;
import java.util.List;

public interface UserInstrumentCommandService {
  void addInstrument(Long userId, Instrument instrument);
  void removeInstrument(Long userId, Instrument instrument);
  void setInstruments(Long userId, List<Instrument> instruments);
}