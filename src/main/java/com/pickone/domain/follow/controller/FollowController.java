package com.pickone.domain.follow.controller;

import com.pickone.domain.follow.dto.FollowRequest;
import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.service.FollowCommandService;
import com.pickone.domain.follow.service.FollowQueryService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {
  private final FollowCommandService commandService;
  private final FollowQueryService queryService;

  @PostMapping
  public ResponseEntity<BaseResponse<FollowResponse>> follow(@RequestBody FollowRequest request) {
    FollowResponse response = commandService.follow(request);
    if (response == null) {
      // 언팔로우 처리되었을 때 성공 메시지
      return BaseResponse.success(SuccessCode.DELETED, null);
    }
    return BaseResponse.success(SuccessCode.CREATED, response);
  }

  @DeleteMapping("/{fromUserId}/{toUserId}")
  public ResponseEntity<BaseResponse<Void>> unfollow(
      @PathVariable Long fromUserId,
      @PathVariable Long toUserId) {
    commandService.unfollow(new FollowRequest(fromUserId, toUserId));
    return BaseResponse.success(SuccessCode.DELETED);
  }

  @GetMapping("/followers/{userId}")
  public ResponseEntity<BaseResponse<List<FollowResponse>>> getFollowers(@PathVariable Long userId) {
    List<FollowResponse> followers = queryService.getFollowers(userId);
    return BaseResponse.success(followers);
  }

  @GetMapping("/followings/{userId}")
  public ResponseEntity<BaseResponse<List<FollowResponse>>> getFollowings(@PathVariable Long userId) {
    List<FollowResponse> followings = queryService.getFollowings(userId);
    return BaseResponse.success(followings);
  }

  @GetMapping("/is-following")
  public ResponseEntity<BaseResponse<Boolean>> isFollowing(
      @RequestParam Long fromUserId,
      @RequestParam Long toUserId) {
    boolean isFollowing = queryService.isFollowing(fromUserId, toUserId);
    return BaseResponse.success(isFollowing);
  }
}
