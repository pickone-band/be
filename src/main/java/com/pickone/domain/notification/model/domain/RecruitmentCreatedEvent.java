package com.pickone.domain.notification.model.domain;

import java.util.List;

// 팔로워들한테
public class RecruitmentCreatedEvent extends BaseUserEvent {

  private final List<Long> followerIds;
  private final String title;

  public RecruitmentCreatedEvent(Long writerId, List<Long> followerIds, String title) {
    super(writerId);
    this.followerIds = followerIds;
    this.title = title;
  }

  public List<Long> getFollowerIds() {
    return followerIds;
  }

  public String getTitle() {
    return title;
  }
}