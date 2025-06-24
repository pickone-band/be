package com.pickone.domain.user.repository;

import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInstrumentJpaRepository extends JpaRepository<UserInstrumentEntity, Long> {

}
