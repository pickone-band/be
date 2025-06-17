package com.PickOne.domain.user.repository.impl;

import com.PickOne.domain.user.dto.UserSearchConditionDto;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserQueryDslRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.PickOne.domain.user.model.entity.QUserEntity.userEntity;
import static com.PickOne.domain.user.model.entity.QUserInstrumentEntity.userInstrumentEntity;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepositoryImpl implements UserQueryDslRepository {

    private final JPAQueryFactory query;

    @Override
    public Page<UserEntity> searchUsers(UserSearchConditionDto cond, Pageable pageable) {

        BooleanBuilder where = new BooleanBuilder();

        // 1. 키워드 검색 (nickname, email)
        if (hasText(cond.keyword())) {
            where.and(
                    userEntity.nickname.containsIgnoreCase(cond.keyword())
                            .or(userEntity.email.containsIgnoreCase(cond.keyword()))
            );
        }

        // 2. 공개 여부 필터
        if (Boolean.TRUE.equals(cond.onlyPublic())) {
            where.and(userEntity.isPublic.isTrue());
        }

        // 3. 단일 enum 필드 (mbti, gender, role)
        if (cond.gender() != null) {
            where.and(userEntity.gender.eq(cond.gender()));
        }
        if (cond.role() != null) {
            where.and(userEntity.role.eq(cond.role()));
        }
        if (cond.mbti() != null) {
            where.and(userEntity.mbti.eq(cond.mbti()));
        }

        // 4. 악기 필터 (다중) → userInstruments 조인 필요
        if (cond.instruments() != null && !cond.instruments().isEmpty()) {
            where.and(userInstrumentEntity.instrument.in(cond.instruments()));
        }

        // 5. 장르 필터 (다중) → genres any()
        if (cond.genres() != null && !cond.genres().isEmpty()) {
            where.and(userEntity.genres.any().in(cond.genres()));
        }

        // 6. 나이 또는 생년월일 범위
        LocalDate today = LocalDate.now();
        if (cond.minAge() != null) {
            where.and(userEntity.birthDate.loe(today.minusYears(cond.minAge())));
        }
        if (cond.maxAge() != null) {
            where.and(userEntity.birthDate.goe(today.minusYears(cond.maxAge() + 1).plusDays(1)));
        }
        if (cond.birthDateFrom() != null) {
            where.and(userEntity.birthDate.goe(cond.birthDateFrom()));
        }
        if (cond.birthDateTo() != null) {
            where.and(userEntity.birthDate.loe(cond.birthDateTo()));
        }

        // 실제 쿼리 실행
        List<UserEntity> content = query
                .selectDistinct(userEntity)
                .from(userEntity)
                .leftJoin(userEntity.userInstruments, userInstrumentEntity).fetchJoin()
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = query
                .select(userEntity.countDistinct())
                .from(userEntity)
                .leftJoin(userEntity.userInstruments, userInstrumentEntity)
                .where(where)
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable,
                () -> count != null ? count : 0L);
    }
}
