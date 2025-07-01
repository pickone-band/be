package com.pickone.domain.user.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import com.pickone.domain.user.repository.UserInstrumentJpaRepository;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Instrument;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Command 구현
@Service
@RequiredArgsConstructor
public class UserInstrumentCommandServiceImpl implements UserInstrumentCommandService {
  private final UserJpaRepository userRepository;
  private final UserInstrumentJpaRepository userInstrumentRepository;

  @Override
  public void addInstrument(Long userId, Instrument instrument) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    UserInstrumentEntity entity = UserInstrumentEntity.builder()
        .user(user)
        .instrument(instrument)
        .build();
    userInstrumentRepository.save(entity);
    user.getInstruments().add(entity); // 양방향 연관관계 유지
  }

  @Override
  public void removeInstrument(Long userId, Instrument instrument) {
    UserInstrumentEntity entity = userInstrumentRepository
        .findByUserIdAndInstrument(userId, instrument)
        .orElseThrow(() -> new RuntimeException("Not found"));
    userInstrumentRepository.delete(entity);
  }

  @Override
  public void setInstruments(Long userId, List<Instrument> instruments) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    userInstrumentRepository.deleteByUserId(userId);
    for (Instrument i : instruments) {
      UserInstrumentEntity entity = UserInstrumentEntity.builder()
          .user(user)
          .instrument(i)
          .build();
      userInstrumentRepository.save(entity);
    }
  }
}
