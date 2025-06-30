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

    // VO 내부 필드 접근: 프로필 닉네임, 이메일
    if (hasText(cond.getKeyword())) {
      where.and(userEntity.profile.nickname.containsIgnoreCase(cond.getKeyword())
          .or(userEntity.profile.email.containsIgnoreCase(cond.getKeyword())));
    }

    // 공개 여부: UserStatus VO 내 필드 (가정: status.isPublic)
    if (Boolean.TRUE.equals(cond.getOnlyPublic())) {
      where.and(userEntity.status.isPublic.isTrue());
    }

    // 단일 enum 필드 (mbti, gender, role)
    if (cond.getGender() != null) {
      where.and(userEntity.profile.gender.eq(cond.getGender()));
    }
    if (cond.getRole() != null) {
      where.and(userEntity.role.eq(cond.getRole()));
    }
    if (cond.getMbti() != null) {
      where.and(userEntity.profile.mbti.eq(cond.getMbti()));
    }

    // 악기 필터 (다중)
    if (cond.getInstruments() != null && !cond.getInstruments().isEmpty()) {
      where.and(userInstrumentEntity.instrument.in(cond.getInstruments()));
    }

    // 장르 필터 (다중) — List<Genre> genres VO 내부
    if (cond.getGenres() != null && !cond.getGenres().isEmpty()) {
      where.and(userEntity.preference.genres.any().in(cond.getGenres()));
    }

    // 나이 또는 생년월일 범위 — birthDate는 UserProfile VO 내 필드
    LocalDate today = LocalDate.now();
    if (cond.getMinAge() != null) {
      where.and(userEntity.profile.birthDate.loe(today.minusYears(cond.getMinAge())));
    }
    if (cond.getMaxAge() != null) {
      where.and(userEntity.profile.birthDate.goe(today.minusYears(cond.getMaxAge() + 1).plusDays(1)));
    }
    if (cond.getBirthDateFrom() != null) {
      where.and(userEntity.profile.birthDate.goe(cond.getBirthDateFrom()));
    }
    if (cond.getBirthDateTo() != null) {
      where.and(userEntity.profile.birthDate.loe(cond.getBirthDateTo()));
    }

    // 실제 쿼리 실행
    List<UserEntity> content = query.selectDistinct(userEntity).from(userEntity)
        .leftJoin(userEntity.instruments, userInstrumentEntity).fetchJoin()
        .where(where)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long count = query.select(userEntity.countDistinct()).from(userEntity)
        .leftJoin(userEntity.instruments, userInstrumentEntity)
        .where(where)
        .fetchOne();

    return PageableExecutionUtils.getPage(content, pageable, () -> count != null ? count : 0L);
  }

  public Page<UserEntity> searchByKeywordAndPublic(String keyword, Boolean onlyPublic, Pageable pageable) {
    BooleanBuilder where = new BooleanBuilder();

    if (keyword != null && !keyword.isBlank()) {
      where.and(
          userEntity.profile.nickname.containsIgnoreCase(keyword)
              .or(userEntity.profile.email.containsIgnoreCase(keyword))
      );
    }

    if (Boolean.TRUE.equals(onlyPublic)) {
      where.and(userEntity.status.isPublic.isTrue());
    }

    // 실제 쿼리 실행
    List<UserEntity> content = query.selectFrom(userEntity)
        .where(where)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long count = query.select(userEntity.count())
        .from(userEntity)
        .where(where)
        .fetchOne();

    return PageableExecutionUtils.getPage(content, pageable, () -> count != null ? count : 0L);
  }
}
