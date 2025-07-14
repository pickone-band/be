package com.pickone.domain.follow.controller;

import com.pickone.domain.follow.dto.FollowRequest;
import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.service.FollowCommandService;
import com.pickone.domain.follow.service.FollowQueryService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
@Tag(name = "Follow", description = "팔로우/언팔로우 및 팔로워/팔로잉 관련 API")
public class FollowController {

  private final FollowCommandService commandService;
  private final FollowQueryService queryService;

  @Operation(summary = "팔로우 요청", description = "특정 사용자를 팔로우하거나, 이미 팔로우 중이면 언팔로우 처리합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<FollowResponse>> follow(
      @AuthenticationPrincipal(expression = "id") Long userId,
      @RequestBody FollowRequest request) {
    log.info("[FollowController] 팔로우 요청 - from: {}, to: {}", userId, request.toUserId());
    FollowResponse response = commandService.follow(new FollowRequest(userId, request.toUserId()));
    return response == null
        ? BaseResponse.success(SuccessCode.DELETED, null)
        : BaseResponse.success(SuccessCode.CREATED, response);
  }

  @Operation(summary = "언팔로우", description = "팔로우 중인 사용자를 언팔로우합니다.")
  @DeleteMapping("/{toUserId}")
  public ResponseEntity<BaseResponse<Void>> unfollow(
      @AuthenticationPrincipal(expression = "id") Long fromUserId,
      @PathVariable Long toUserId) {
    log.info("[FollowController] 언팔로우 요청 - from: {}, to: {}", fromUserId, toUserId);
    commandService.unfollow(new FollowRequest(fromUserId, toUserId));
    return BaseResponse.success(SuccessCode.DELETED);
  }

  @Operation(summary = "팔로워 목록 조회", description = "해당 사용자를 팔로우 중인 사용자 목록을 조회합니다.")
  @GetMapping("/followers")
  public ResponseEntity<BaseResponse<List<FollowResponse>>> getFollowers(
      @RequestParam Long userId) {
    log.info("[FollowController] 팔로워 목록 조회 - 대상 ID: {}", userId);
    return BaseResponse.success(queryService.getFollowers(userId));
  }

  @Operation(summary = "팔로잉 목록 조회", description = "해당 사용자가 팔로우 중인 사용자 목록을 조회합니다.")
  @GetMapping("/followings")
  public ResponseEntity<BaseResponse<List<FollowResponse>>> getFollowings(
      @RequestParam Long userId) {
    log.info("[FollowController] 팔로잉 목록 조회 - 사용자 ID: {}", userId);
    return BaseResponse.success(queryService.getFollowings(userId));
  }

  @Operation(summary = "팔로우 여부 확인", description = "특정 사용자(from)가 다른 사용자(to)를 팔로우하고 있는지 확인합니다.")
  @GetMapping("/is-following")
  public ResponseEntity<BaseResponse<Boolean>> isFollowing(
      @RequestParam Long fromUserId,
      @RequestParam Long toUserId) {
    log.info("[FollowController] 팔로우 여부 확인 - from: {}, to: {}", fromUserId, toUserId);
    return BaseResponse.success(queryService.isFollowing(fromUserId, toUserId));
  }

}
