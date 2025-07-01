package com.pickone.domain.user.repository;

import com.pickone.domain.user.model.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
  boolean existsByProfileEmail(String email);
  Optional<UserEntity> findByProfileEmail(String email);
}
