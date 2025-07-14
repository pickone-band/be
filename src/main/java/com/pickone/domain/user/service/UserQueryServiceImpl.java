package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.UserIdResponse;
import com.pickone.domain.user.dto.UserResponse;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.mapper.UserMapper;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import java.util.List;
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

  @Override
  public List<UserIdResponse> getUserIdsByNicknames(List<String> nicknames) {
    log.info("[UserQueryService] 닉네임으로 사용자 ID 조회 요청: nicknames={}", nicknames);

    List<User> users = userRepository.findByNicknameIn(nicknames);

    log.info("[UserQueryService] 조회된 사용자 수: {}", users.size());

    return users.stream()
        .map(user -> {
          Long id = user.getId();
          String nickname = user.getProfile().getNickname();
          log.debug("[UserQueryService] 사용자 매핑 - id: {}, nickname: {}", id, nickname);
          return new UserIdResponse(id, nickname);
        })
        .toList();
  }

}
