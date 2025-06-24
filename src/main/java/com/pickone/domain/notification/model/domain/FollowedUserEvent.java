package com.pickone.domain.notification.model.domain;

public class FollowedUserEvent extends BaseUserEvent {

  private final Long followedUserId;

  public FollowedUserEvent(Long followerId, Long followedUserId) {
    super(followerId);
    this.followedUserId = followedUserId;
  }

  public Long getFollowedUserId() {
    return followedUserId;
  }
}