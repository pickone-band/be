package com.pickone.domain.user.controller;

import com.pickone.domain.user.dto.*;
import com.pickone.domain.user.service.UserCommandService;
import com.pickone.domain.user.service.UserQueryService;
import com.pickone.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "회원 정보 관련 API")
@Slf4j
public class UserController {

  private final UserCommandService commandService;
  private final UserQueryService queryService;

  @Operation(summary = "회원 가입", description = "신규 사용자를 등록합니다.")
  @PostMapping("/signup")
  public ResponseEntity<BaseResponse<UserResponse>> signup(
      @RequestBody SignupRequest request) {
    log.info("[UserController] 회원 가입 요청: email={}, nickname={}", request.email(), request.nickname());
    return BaseResponse.success(commandService.signup(request));
  }

  @Operation(summary = "프로필 수정", description = "사용자의 닉네임, MBTI, 자기소개를 수정합니다.")
  @PatchMapping("/profile")
  public ResponseEntity<BaseResponse<Void>> updateProfile(
      @Parameter(hidden = true) @AuthenticationPrincipal(expression = "id") Long userId,
      @RequestBody UpdateProfileRequest request) {
    log.info("[UserController] 프로필 수정 요청: userId={}, nickname={}, mbti={}, introduction={}",
        userId, request.nickname(), request.mbti(), request.introduction());
    commandService.updateProfile(userId, request);
    return BaseResponse.success();
  }

  @Operation(summary = "선호 장르 수정", description = "사용자의 장르 선호 정보를 수정합니다.")
  @PatchMapping("/{userId}/preference")
  public ResponseEntity<BaseResponse<Void>> updatePreference(
      @Parameter(hidden = true) @AuthenticationPrincipal(expression = "id") Long userId,
      @RequestBody UpdatePreferenceRequest request) {
    log.info("[UserController] 선호 장르 수정 요청: userId={}, genres={}", userId, request.genres());
    commandService.updatePreference(userId, request);
    return BaseResponse.success();
  }

  @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
  @PatchMapping("/password")
  public ResponseEntity<BaseResponse<Void>> changePassword(
      @Parameter(hidden = true) @AuthenticationPrincipal(expression = "id") Long userId,
      @RequestBody ChangePasswordRequest request) {
    log.info("[UserController] 비밀번호 변경 요청: userId={}", userId);
    commandService.changePassword(userId, request);
    return BaseResponse.success();
  }

  @Operation(summary = "사용자 잠금", description = "해당 사용자를 잠금 처리합니다.")
  @PatchMapping("/lock")
  public ResponseEntity<BaseResponse<Void>> lockUser(
      @Parameter(hidden = true) @AuthenticationPrincipal(expression = "id") Long userId) {
    log.info("[UserController] 사용자 잠금 요청: userId={}", userId);
    commandService.lockUser(userId);
    return BaseResponse.success();
  }

  @Operation(summary = "회원 탈퇴", description = "해당 사용자를 탈퇴 처리합니다.")
  @DeleteMapping("/{userId}")
  public ResponseEntity<BaseResponse<Void>> deleteUser(
      @Parameter(description = "사용자 ID") @PathVariable Long userId) {
    log.info("[UserController] 회원 탈퇴 요청: userId={}", userId);
    commandService.deleteUser(userId);
    return BaseResponse.success();
  }

  @Operation(summary = "회원 상세 조회", description = "사용자의 상세 정보를 조회합니다.")
  @GetMapping("/{userId}")
  public ResponseEntity<BaseResponse<UserResponse>> getUser(
      @Parameter(description = "사용자 ID") @PathVariable Long userId) {
    log.info("[UserController] 사용자 정보 조회 요청: userId={}", userId);
    return BaseResponse.success(queryService.getUser(userId));
  }

  @Operation(summary = "닉네임으로 사용자 ID 조회", description = "닉네임 목록으로 사용자 ID 리스트를 조회합니다.")
  @GetMapping("/resolve-ids")
  public ResponseEntity<BaseResponse<List<UserIdResponse>>> getUserIdsByNicknames(
      @RequestParam List<String> nicknames
  ) {
    log.info("[UserController] 닉네임으로 ID 조회 요청: {}", nicknames);
    return BaseResponse.success(queryService.getUserIdsByNicknames(nicknames));
  }
}
