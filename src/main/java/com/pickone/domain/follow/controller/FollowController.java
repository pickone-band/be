package com.pickone.domain.follow.controller;

import com.pickone.domain.follow.dto.FollowRequest;
import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.service.FollowCommandService;
import com.pickone.domain.follow.service.FollowQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {
  private final FollowCommandService commandService;
  private final FollowQueryService queryService;

  @PostMapping
  public FollowResponse follow(@RequestBody FollowRequest request) {
    return commandService.follow(request);
  }

  @DeleteMapping
  public void unfollow(@RequestBody FollowRequest request) {
    commandService.unfollow(request);
  }

  @GetMapping("/followers/{userId}")
  public List<FollowResponse> getFollowers(@PathVariable Long userId) {
    return queryService.getFollowers(userId);
  }

  @GetMapping("/followings/{userId}")
  public List<FollowResponse> getFollowings(@PathVariable Long userId) {
    return queryService.getFollowings(userId);
  }

  @GetMapping("/is-following")
  public boolean isFollowing(
      @RequestParam Long fromUserId,
      @RequestParam Long toUserId) {
    return queryService.isFollowing(fromUserId, toUserId);
  }
}
