package com.PickOne.domain.follow.dto;

public record FollowRequest(
        Long followerId,
        Long followingId
) {}