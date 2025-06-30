package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.UserSearchConditionDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.user.repository.UserQueryDslRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserReader {

  private final UserJpaRepository userJpaRepository;
  private final UserQueryDslRepository userQueryDslRepository;

  @Transactional(readOnly = true)
  public UserEntity findById(Long id) {
    log.info("사용자 조회: id={}", id);
    return userJpaRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("사용자 정보 없음: id={}", id);
          return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        });
  }

  @Transactional(readOnly = true)
  public UserEntity findByEmail(String email) {
    log.info("이메일로 사용자 조회: {}", email);
    return userJpaRepository.findByProfile_Email(email)
        .orElseThrow(() -> {
          log.warn("이메일로 사용자 조회 실패: {}", email);
          return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        });
  }

  @Transactional(readOnly = true)
  public Page<UserEntity> searchUsers(UserSearchConditionDto condition, Pageable pageable) {
    return userQueryDslRepository.searchByKeywordAndPublic(
        condition.getKeyword(),
        condition.getOnlyPublic(),
        pageable
    );
  }

}
