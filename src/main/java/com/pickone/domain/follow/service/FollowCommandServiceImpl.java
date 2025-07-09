package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowRequest;
import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.entity.UserFollow;
import com.pickone.domain.follow.mapper.FollowMapper;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import com.pickone.domain.notification.event.FollowedUserEvent;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowCommandServiceImpl implements FollowCommandService {

  private final UserJpaRepository userRepository;
  private final UserFollowJpaRepository followRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final FollowMapper followMapper;

  @Transactional
  @Override
  public FollowResponse follow(FollowRequest request) {
    User fromUser = userRepository.findById(request.fromUserId())
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    User toUser = userRepository.findById(request.toUserId())
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    UserFollow entity = UserFollow.create(fromUser, toUser);
    UserFollow saved = followRepository.save(entity);

    log.info("[FollowCommandService] 팔로우 완료 - id: {}, from: {}, to: {}",
        saved.getId(), fromUser.getId(), toUser.getId());

    eventPublisher.publishEvent(
        new FollowedUserEvent(toUser.getId(), "새로운 팔로워가 생겼습니다!")
    );

    return followMapper.toDtoWithNickname(saved, fromUser.getProfile().getNickname());
  }

  @Transactional
  @Override
  public void unfollow(FollowRequest request) {
    followRepository.deleteByFromUserIdAndToUserId(request.fromUserId(), request.toUserId());
    log.info("[FollowCommandService] 언팔로우 완료 - from: {}, to: {}", request.fromUserId(), request.toUserId());
  }
}
