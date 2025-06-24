package com.pickone.domain.user.repository;

import com.pickone.domain.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findByNickname(String nickname);

  List<UserEntity> findByIsPublicTrue();

  boolean existsByEmail(String email);
}