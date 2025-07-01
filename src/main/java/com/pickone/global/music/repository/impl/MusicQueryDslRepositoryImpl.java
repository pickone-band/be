package com.pickone.global.music.repository.impl;

import com.pickone.global.music.model.entity.QMusicEntity;
import com.pickone.global.music.repository.MusicQueryDslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MusicQueryDslRepositoryImpl implements MusicQueryDslRepository {

  private final JPAQueryFactory query;

  @Override
  public boolean existsByUserIdAndPlatformTrackId(Long userId, String platformTrackId) {
    QMusicEntity musicEntity = QMusicEntity.musicEntity;
    Integer fetchOne = query.selectOne()
        .from(musicEntity)
        .where(
            musicEntity.user.id.eq(userId),
            musicEntity.music.platformTrackId.eq(platformTrackId)
        )
        .fetchFirst();
    return fetchOne != null;
  }
}
