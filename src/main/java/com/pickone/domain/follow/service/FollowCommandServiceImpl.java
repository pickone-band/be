package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowRequest;
import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.model.entity.UserFollow;
import com.pickone.domain.follow.model.mapper.FollowMapper;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowCommandServiceImpl implements FollowCommandService {
  private final UserFollowJpaRepository followRepository;

  @Transactional
  @Override
  public FollowResponse follow(FollowRequest request) {
    if (followRepository.existsByFromUserIdAndToUserId(request.fromUserId(), request.toUserId())) {
      throw new IllegalArgumentException("Already followed.");
    }
    UserFollow entity = UserFollow.of(request.fromUserId(), request.toUserId());
    UserFollow saved = followRepository.save(entity);
    return FollowMapper.toDto(saved);
  }

  @Transactional
  @Override
  public void unfollow(FollowRequest request) {
    followRepository.deleteByFromUserIdAndToUserId(request.fromUserId(), request.toUserId());
  }
}
