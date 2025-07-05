package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowRequest;
import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.model.entity.UserFollow;
import com.pickone.domain.follow.model.mapper.FollowMapper;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import com.pickone.domain.notification.event.FollowedUserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowCommandServiceImpl implements FollowCommandService {
  private final UserFollowJpaRepository followRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  public FollowResponse follow(FollowRequest request) {
    boolean alreadyFollowing = followRepository.existsByFromUserIdAndToUserId(request.fromUserId(), request.toUserId());
    if (alreadyFollowing) {
      followRepository.deleteByFromUserIdAndToUserId(request.fromUserId(), request.toUserId());
      return null;
    }
    UserFollow entity = UserFollow.of(request.fromUserId(), request.toUserId());
    UserFollow saved = followRepository.save(entity);

    String message = "새로운 팔로워가 생겼습니다!";
    eventPublisher.publishEvent(new FollowedUserEvent(request.toUserId(), message));

    return FollowMapper.toDto(saved);
  }

  @Transactional
  @Override
  public void unfollow(FollowRequest request) {
    followRepository.deleteByFromUserIdAndToUserId(request.fromUserId(), request.toUserId());
  }
}
