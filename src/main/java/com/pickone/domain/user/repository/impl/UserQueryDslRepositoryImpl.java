package com.pickone.domain.user.repository.impl;

import com.pickone.domain.user.dto.UserSearchConditionDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserQueryDslRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.pickone.domain.user.model.entity.QUserEntity.userEntity;
import static com.pickone.domain.user.model.entity.QUserInstrumentEntity.userInstrumentEntity;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepositoryImpl implements UserQueryDslRepository {

  private final JPAQueryFactory query;

  @Override
  public Page<UserEntity> searchUsers(UserSearchConditionDto cond, Pageable pageable) {

    BooleanBuilder where = new BooleanBuilder();

    // 1. 키워드 검색 (nickname, email)
    if (hasText(cond.getKeyword())) {
      where.and(userEntity.nickname.containsIgnoreCase(cond.getKeyword())
          .or(userEntity.email.containsIgnoreCase(cond.getKeyword())));
    }

    // 2. 공개 여부 필터
    if (Boolean.TRUE.equals(cond.getOnlyPublic())) {
      where.and(userEntity.isPublic.isTrue());
    }

    // 3. 단일 enum 필드 (mbti, gender, role)
    if (cond.getGender() != null) {
      where.and(userEntity.gender.eq(cond.getGender()));
    }
    if (cond.getRole() != null) {
      where.and(userEntity.role.eq(cond.getRole()));
    }
    if (cond.getMbti() != null) {
      where.and(userEntity.mbti.eq(cond.getMbti()));
    }

    // 4. 악기 필터 (다중) → userInstruments 조인 필요
    if (cond.getInstruments() != null && !cond.getInstruments().isEmpty()) {
      where.and(userInstrumentEntity.instrument.in(cond.getInstruments()));
    }

    // 5. 장르 필터 (다중)
    if (cond.getGenres() != null && !cond.getGenres().isEmpty()) {
      where.and(userEntity.genres.any().in(cond.getGenres()));
    }

    // 6. 나이 또는 생년월일 범위
    LocalDate today = LocalDate.now();
    if (cond.getMinAge() != null) {
      where.and(userEntity.birthDate.loe(today.minusYears(cond.getMinAge())));
    }
    if (cond.getMaxAge() != null) {
      where.and(userEntity.birthDate.goe(today.minusYears(cond.getMaxAge() + 1).plusDays(1)));
    }
    if (cond.getBirthDateFrom() != null) {
      where.and(userEntity.birthDate.goe(cond.getBirthDateFrom()));
    }
    if (cond.getBirthDateTo() != null) {
      where.and(userEntity.birthDate.loe(cond.getBirthDateTo()));
    }

    // 실제 쿼리 실행
    List<UserEntity> content = query.selectDistinct(userEntity).from(userEntity)
        .leftJoin(userEntity.userInstruments, userInstrumentEntity).fetchJoin()
        .where(where)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long count = query.select(userEntity.countDistinct()).from(userEntity)
        .leftJoin(userEntity.userInstruments, userInstrumentEntity)
        .where(where)
        .fetchOne();

    return PageableExecutionUtils.getPage(content, pageable, () -> count != null ? count : 0L);
  }
}