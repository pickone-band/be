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
    boolean alreadyFollowing = followRepository.existsByFromUserIdAndToUserId(request.fromUserId(), request.toUserId());

    if (alreadyFollowing) {
      // 이미 팔로우 중이면 언팔로우 처리 (토글 방식)
      followRepository.deleteByFromUserIdAndToUserId(request.fromUserId(), request.toUserId());
      // 비어있는 FollowResponse 반환 or null로 처리 가능 (여기선 null로 처리)
      return null;
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
