package com.PickOne.domain.follow.service;

import com.PickOne.domain.follow.model.entity.UserFollow;
import com.PickOne.domain.follow.repository.UserFollowJpaRepository;
import com.PickOne.domain.notification.model.domain.FollowedUserEvent;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserJpaRepository userJpaRepository;
    private final UserFollowJpaRepository userFollowJpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException(ErrorCode.CANNOT_FOLLOW_SELF);
        }

        UserEntity follower = userJpaRepository.findById(followerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        UserEntity following = userJpaRepository.findById(followingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        if (userFollowJpaRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new BusinessException(ErrorCode.ALREADY_FOLLOWING);
        }

        userFollowJpaRepository.save(new UserFollow(follower, following));
        eventPublisher.publishEvent(new FollowedUserEvent(follower.getId(), following.getId()));
    }

    public void unfollow(Long followerId, Long followingId) {
        UserEntity follower = userJpaRepository.findById(followerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        UserEntity following = userJpaRepository.findById(followingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        UserFollow follow = userFollowJpaRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new BusinessException(ErrorCode.FOLLOW_RELATION_NOT_FOUND));

        userFollowJpaRepository.delete(follow);
    }

    public List<UserEntity> getFollowers(Long userId) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        return userFollowJpaRepository.findAllByFollowing(user).stream()
                .map(UserFollow::getFollower)
                .toList();
    }

    public List<UserEntity> getFollowings(Long userId) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        return userFollowJpaRepository.findAllByFollower(user).stream()
                .map(UserFollow::getFollowing)
                .toList();
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        UserEntity follower = userJpaRepository.findById(followerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        UserEntity following = userJpaRepository.findById(followingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        return userFollowJpaRepository.existsByFollowerAndFollowing(follower, following);
    }

    public int getFollowerCount(Long userId) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        return userFollowJpaRepository.findAllByFollowing(user).size();
    }

    public int getFollowingCount(Long userId) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        return userFollowJpaRepository.findAllByFollower(user).size();
    }
}
