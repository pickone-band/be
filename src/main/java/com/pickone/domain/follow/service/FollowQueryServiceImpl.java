package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.mapper.FollowMapper;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowQueryServiceImpl implements FollowQueryService {

  private final UserFollowJpaRepository followRepository;
  private final FollowMapper followMapper;

  @Override
  public List<FollowResponse> getFollowers(Long userId) {
    log.info("[FollowQueryService] 팔로워 목록 조회 요청 - 대상 ID: {}", userId);

    return followRepository.findByToUserId(userId).stream()
        .map(follow -> {
          User fromUser = follow.getFromUser();
          return followMapper.toDtoWithNickname(
              follow, fromUser.getProfile().getNickname()
          );
        })
        .toList();
  }

  @Override
  public List<FollowResponse> getFollowings(Long userId) {
    log.info("[FollowQueryService] 팔로잉 목록 조회 요청 - 사용자 ID: {}", userId);

    return followRepository.findByFromUserId(userId).stream()
        .map(follow -> {
          User toUser = follow.getToUser();
          return followMapper.toDtoWithNickname(
              follow, toUser.getProfile().getNickname()
          );
        })
        .toList();
  }

  @Override
  public boolean isFollowing(Long fromUserId, Long toUserId) {
    boolean result = followRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId);
    log.info("[FollowQueryService] 팔로우 여부 확인 - from: {}, to: {}, 결과: {}", fromUserId, toUserId, result);
    return result;
  }
}
