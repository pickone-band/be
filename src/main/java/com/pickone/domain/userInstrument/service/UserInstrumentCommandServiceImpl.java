package com.pickone.domain.userInstrument.service;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.userInstrument.entity.UserInstrument;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.userInstrument.repository.UserInstrumentJpaRepository;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Command 구현
@Service
@RequiredArgsConstructor
public class UserInstrumentCommandServiceImpl implements UserInstrumentCommandService {
  private final UserJpaRepository userRepository;
  private final UserInstrumentJpaRepository userInstrumentRepository;

  @Override
  public void addInstrument(Long userId, Instrument instrument) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    UserInstrument entity = UserInstrument.create(user, instrument, Proficiency.NEVER_PLAYED);
    userInstrumentRepository.save(entity);
    user.getInstruments().add(entity);
  }

  @Override
  public void removeInstrument(Long userId, Instrument instrument) {
    UserInstrument entity = userInstrumentRepository
        .findByUserIdAndInstrument(userId, instrument)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    userInstrumentRepository.delete(entity);
  }

  @Transactional
  @Override
  public void setInstruments(Long userId, List<Instrument> instruments) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    userInstrumentRepository.deleteByUserId(userId);

    if (instruments == null || instruments.isEmpty()) {
      // 악기 없음 상태 유지
      return;
    }

    for (Instrument instrument : instruments) {
      UserInstrument entity = UserInstrument.create(user, instrument, Proficiency.NEVER_PLAYED);
      userInstrumentRepository.save(entity);
    }
  }
}
