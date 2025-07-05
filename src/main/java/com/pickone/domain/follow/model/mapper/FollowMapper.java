package com.pickone.domain.follow.model.mapper;

import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.model.entity.UserFollow;

public class FollowMapper {

  public static FollowResponse toDto(UserFollow entity) {
    return new FollowResponse(
        entity.getId(),
        entity.getFromUserId(),
        entity.getToUserId(),
        null // nickname은 기본 toDto에선 포함하지 않음
    );
  }

  public static FollowResponse toDtoWithNickname(UserFollow entity, String nickname) {
    return new FollowResponse(
        entity.getId(),
        entity.getFromUserId(),
        entity.getToUserId(),
        nickname
    );
  }
}
