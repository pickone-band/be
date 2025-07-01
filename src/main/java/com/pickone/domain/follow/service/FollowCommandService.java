package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowRequest;
import com.pickone.domain.follow.dto.FollowResponse;

public interface FollowCommandService {
  FollowResponse follow(FollowRequest request);
  void unfollow(FollowRequest request);
}
