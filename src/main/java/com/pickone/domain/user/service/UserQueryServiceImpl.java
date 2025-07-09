package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.UserResponse;
import com.pickone.domain.user.mapper.UserMapper;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserQueryServiceImpl implements UserQueryService {

  private final UserJpaRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserResponse getUser(Long userId) {
    log.info("[UserQueryService] 사용자 조회 요청: userId={}", userId);

    var user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    return userMapper.toDto(user);
  }
}
