package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.model.entity.UserFollow;
import com.pickone.domain.follow.model.mapper.FollowMapper;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowQueryServiceImpl implements FollowQueryService {

  private final UserFollowJpaRepository followRepository;
  private final UserJpaRepository userRepository;

  @Override
  public List<FollowResponse> getFollowers(Long userId) {
    return followRepository.findByToUserId(userId)
        .stream()
        .map(follow -> {
          UserEntity fromUser = userRepository.findById(follow.getFromUserId())
              .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
          return FollowMapper.toDtoWithNickname(follow, fromUser.getProfile().getNickname());
        })
        .toList();
  }

  @Override
  public List<FollowResponse> getFollowings(Long userId) {
    return followRepository.findByFromUserId(userId)
        .stream()
        .map(follow -> {
          UserEntity toUser = userRepository.findById(follow.getToUserId())
              .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
          return FollowMapper.toDtoWithNickname(follow, toUser.getProfile().getNickname());
        })
        .toList();
  }

  @Override
  public boolean isFollowing(Long fromUserId, Long toUserId) {
    return followRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId);
  }
}
