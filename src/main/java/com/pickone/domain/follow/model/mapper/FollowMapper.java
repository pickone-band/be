package com.pickone.domain.follow.model.mapper;

import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.model.entity.UserFollow;

public class FollowMapper {
  public static FollowResponse toDto(UserFollow entity) {
    return new FollowResponse(
        entity.getId(),
        entity.getFromUserId(),
        entity.getToUserId()
    );
  }
}
