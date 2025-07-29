package com.pickone.domain.user.repository;


import com.pickone.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {
  Optional<User> findByProfileEmail(String email);
  boolean existsByProfileEmail(String email);
  boolean existsByProfileNickname(String nickname);
  @Query("SELECT u FROM User u WHERE u.profile.nickname IN :nicknames")
  List<User> findByNicknameIn(@Param("nicknames") List<String> nicknames);
  List<User> findByIdBetween(Long start, Long end);

}
