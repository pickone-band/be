package com.pickone.domain.userInstrument.repository;

import com.pickone.domain.userInstrument.entity.UserInstrument;
import com.pickone.global.common.enums.Instrument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInstrumentJpaRepository extends JpaRepository<UserInstrument, Long> {
  List<UserInstrument> findByUserId(Long userId);
  Optional<UserInstrument> findByUserIdAndInstrument(Long userId, Instrument instrument);
  void deleteByUserId(Long userId);
}