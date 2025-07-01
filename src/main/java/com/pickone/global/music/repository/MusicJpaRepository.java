package com.pickone.global.music.repository;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.music.model.entity.MusicEntity;
import com.pickone.global.music.model.vo.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicJpaRepository extends JpaRepository<MusicEntity, Long>, MusicQueryDslRepository {
  boolean existsByUserAndMusic(UserEntity user, Music music);
}
