package com.pickone.domain.follow.service;

import com.pickone.domain.follow.model.entity.UserFollow;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import com.pickone.domain.notification.model.domain.FollowedUserEvent;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

  private final UserJpaRepository userJpaRepository;
  private final UserFollowJpaRepository userFollowJpaRepository;
  private final ApplicationEventPublisher eventPublisher;

  public void follow(Long followerId, Long followingId) {
    if (followerId.equals(followingId)) {
      log.warn("자기 자신을 팔로우하려는 시도: userId={}", followerId);
      throw new BusinessException(ErrorCode.CANNOT_FOLLOW_SELF);
    }

    UserEntity follower = findUserOrThrow(followerId);
    UserEntity following = findUserOrThrow(followingId);

    if (userFollowJpaRepository.existsByFollowerAndFollowing(follower, following)) {
      log.warn("이미 팔로우 상태: followerId={}, followingId={}", followerId, followingId);
      throw new BusinessException(ErrorCode.ALREADY_FOLLOWING);
    }

    userFollowJpaRepository.save(new UserFollow(follower, following));
    eventPublisher.publishEvent(new FollowedUserEvent(followerId, followingId));
    log.info("팔로우 성공: followerId={}, followingId={}", followerId, followingId);
  }

  public void unfollow(Long followerId, Long followingId) {
    UserEntity follower = findUserOrThrow(followerId);
    UserEntity following = findUserOrThrow(followingId);

    UserFollow follow = userFollowJpaRepository.findByFollowerAndFollowing(follower, following)
        .orElseThrow(() -> {
          log.warn("팔로우 관계 없음: followerId={}, followingId={}", followerId, followingId);
          return new BusinessException(ErrorCode.FOLLOW_RELATION_NOT_FOUND);
        });

    userFollowJpaRepository.delete(follow);
    log.info("언팔로우 완료: followerId={}, followingId={}", followerId, followingId);
  }

  public List<UserEntity> getFollowers(Long userId) {
    return userFollowJpaRepository.findAllByFollowing(findUserOrThrow(userId))
        .stream().map(UserFollow::getFollower).toList();
  }

  public List<UserEntity> getFollowings(Long userId) {
    return userFollowJpaRepository.findAllByFollower(findUserOrThrow(userId))
        .stream().map(UserFollow::getFollowing).toList();
  }

  public boolean isFollowing(Long followerId, Long followingId) {
    return userFollowJpaRepository.existsByFollowerAndFollowing(
        findUserOrThrow(followerId), findUserOrThrow(followingId)
    );
  }

  public int getFollowerCount(Long userId) {
    return userFollowJpaRepository.findAllByFollowing(findUserOrThrow(userId)).size();
  }

  public int getFollowingCount(Long userId) {
    return userFollowJpaRepository.findAllByFollower(findUserOrThrow(userId)).size();
  }

  private UserEntity findUserOrThrow(Long userId) {
    return userJpaRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자 조회 실패: userId={}", userId);
          return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        });
  }
}
