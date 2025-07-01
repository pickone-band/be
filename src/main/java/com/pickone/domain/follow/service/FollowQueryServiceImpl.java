package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.model.mapper.FollowMapper;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowQueryServiceImpl implements FollowQueryService {
  private final UserFollowJpaRepository followRepository;

  @Override
  public List<FollowResponse> getFollowers(Long userId) {
    return followRepository.findByToUserId(userId)
        .stream().map(FollowMapper::toDto).toList();
  }

  @Override
  public List<FollowResponse> getFollowings(Long userId) {
    return followRepository.findByFromUserId(userId)
        .stream().map(FollowMapper::toDto).toList();
  }

  @Override
  public boolean isFollowing(Long fromUserId, Long toUserId) {
    return followRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId);
  }
}
