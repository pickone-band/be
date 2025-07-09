package com.pickone.domain.user.repository;


import com.pickone.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
  Optional<User> findByProfileEmail(String email);
  boolean existsByProfileEmail(String email);
  boolean existsByProfileNickname(String nickname);
}
