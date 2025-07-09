package com.pickone.global.music.repository;

import com.pickone.domain.user.entity.User;

import com.pickone.global.music.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicJpaRepository extends JpaRepository<Music, Long>, MusicQueryDslRepository {
  boolean existsByUserAndId(User user, Long musicId); //
}
