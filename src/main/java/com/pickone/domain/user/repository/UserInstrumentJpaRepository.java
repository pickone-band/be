package com.pickone.domain.user.repository;

import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import com.pickone.global.common.enums.Instrument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInstrumentJpaRepository extends JpaRepository<UserInstrumentEntity, Long> {
  List<UserInstrumentEntity> findByUserId(Long userId);
  Optional<UserInstrumentEntity> findByUserIdAndInstrument(Long userId, Instrument instrument);
  void deleteByUserId(Long userId);
}