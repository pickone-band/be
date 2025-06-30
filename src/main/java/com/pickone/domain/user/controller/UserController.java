package com.pickone.domain.user.controller;

import com.pickone.domain.user.dto.UserResponseDto;
import com.pickone.domain.user.dto.UserSearchConditionDto;
import com.pickone.domain.user.dto.UserUpdateRequestDto;
import com.pickone.domain.user.model.domain.Role;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.service.UserService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.exception.SuccessCode;
import com.pickone.global.security.model.entity.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User API", description = "회원 정보 조회, 수정, 탈퇴 API")
public class UserController {

  private final UserService userService;

  @Operation(summary = "회원 정보 조회", description = "ID로 회원 정보를 조회합니다.")
  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
    log.info("회원 조회 요청: id={}", id);
    UserEntity user = userService.findById(id);
    return BaseResponse.success(UserResponseDto.from(user)); // ResponseEntity는 이미 포함됨
  }


  @Operation(summary = "회원 정보 수정", description = "로그인한 사용자 본인만 자신의 정보를 수정할 수 있습니다.")
  @PutMapping("/{id}")
  public ResponseEntity<BaseResponse<Void>> updateUser(
      @PathVariable Long id,
      @RequestBody @Valid UserUpdateRequestDto request,
      @AuthenticationPrincipal UserPrincipal principal) {

    validateSelfAccess(id, principal);
    log.info("회원 수정 요청: userId={}", id);

    userService.updateUser(id, request);
    return BaseResponse.success(SuccessCode.UPDATED);
  }


  @Operation(summary = "회원 탈퇴", description = "본인 또는 관리자가 사용자의 계정을 삭제합니다.")
  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponse<Void>> deleteUser(
      @PathVariable Long id,
      @AuthenticationPrincipal UserPrincipal principal) {

    validateSelfOrAdminAccess(id, principal);
    log.info("회원 탈퇴 요청: userId={}", id);

    userService.deleteUser(id);
    return BaseResponse.success(SuccessCode.DELETED);
  }

  @Operation(summary = "키워드로 사용자 검색", description = "닉네임 또는 이메일로 사용자 검색합니다.")
  @GetMapping("/search")
  public ResponseEntity<BaseResponse<Page<UserResponseDto>>> searchUsers(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "true") boolean onlyPublic,
      Pageable pageable) {

    log.info("사용자 검색 요청: keyword={}, onlyPublic={}", keyword, onlyPublic);

    UserSearchConditionDto condition = UserSearchConditionDto.builder()
        .keyword(keyword)
        .onlyPublic(onlyPublic)
        .build();

    Page<UserEntity> users = userService.searchUsers(condition, pageable);
    return BaseResponse.success(users.map(UserResponseDto::from));
  }

  // --- 권한 체크 메서드 분리 (private) ---

  private void validateSelfAccess(Long targetUserId, UserPrincipal principal) {
    if (!targetUserId.equals(principal.getUserId())) {
      log.warn("권한 거부: 본인 외 정보 수정 시도 userId={}, principalId={}", targetUserId, principal.getUserId());
      throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
    }
  }

  private void validateSelfOrAdminAccess(Long targetUserId, UserPrincipal principal) {
    boolean isSelf = targetUserId.equals(principal.getUserId());
    boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
    if (!isSelf && !isAdmin) {
      log.warn("권한 거부: 본인 또는 관리자만 가능 userId={}, principalId={}", targetUserId, principal.getUserId());
      throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
    }
  }
}


