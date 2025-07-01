package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowResponse;
import java.util.List;

public interface FollowQueryService {
  List<FollowResponse> getFollowers(Long userId);
  List<FollowResponse> getFollowings(Long userId);
  boolean isFollowing(Long fromUserId, Long toUserId);
}
