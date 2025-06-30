package com.pickone.domain.term.repository.impl;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.pickone.domain.term.model.entity.QTermEntity.termEntity;

@Repository
@RequiredArgsConstructor
public class TermQueryRepositoryImpl implements TermQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<TermEntity> findRequiredTermsEffectiveAfter(LocalDateTime threshold) {
    return queryFactory.selectFrom(termEntity)
        .where(
            termEntity.required.isTrue(),
            termEntity.effectiveDate.after(threshold)
        )
        .orderBy(termEntity.effectiveDate.desc())
        .fetch();
  }

  @Override
  public List<TermEntity> findByTitleKeyword(String keyword) {
    return queryFactory.selectFrom(termEntity)
        .where(termEntity.title.containsIgnoreCase(keyword))
        .fetch();
  }
}
