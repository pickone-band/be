package com.pickone.domain.follow.dto;

public record FollowResponse(
    Long id,
    Long fromUserId,
    Long toUserId,
    String nickname
) {}