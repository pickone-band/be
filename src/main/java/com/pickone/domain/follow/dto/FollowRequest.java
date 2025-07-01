package com.pickone.domain.follow.dto;

public record FollowRequest(
    Long fromUserId,
    Long toUserId
) {}
