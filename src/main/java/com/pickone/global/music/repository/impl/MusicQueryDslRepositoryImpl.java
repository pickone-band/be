package com.pickone.global.music.repository.impl;

import com.pickone.global.music.entity.QMusic;
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
    QMusic m = QMusic.music;

    return query.selectOne()
        .from(m)
        .where(
            m.user.id.eq(userId),
            m.musicInfo.platformTrackId.eq(platformTrackId)
        )
        .fetchFirst() != null;
  }
}