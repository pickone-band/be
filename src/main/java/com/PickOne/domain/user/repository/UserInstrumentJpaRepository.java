package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.entity.UserInstrumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInstrumentJpaRepository  extends JpaRepository<UserInstrumentEntity, Long> {
}
