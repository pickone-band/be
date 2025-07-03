package com.pickone.domain.user.repository;

import com.pickone.domain.user.model.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByProfileEmail(String email);
  boolean existsByProfileEmail(String email);
  boolean existsByProfileNickname(String nickname);
}
