package com.pickone.domain.consent.controller;

import com.pickone.domain.consent.dto.ConsentRequest;
import com.pickone.domain.consent.dto.ConsentResponse;
import com.pickone.domain.consent.service.ConsentCommandService;
import com.pickone.domain.consent.service.ConsentQueryService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.security.entity.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/consents")
@Tag(name = "Consent", description = "사용자 약관 동의 API")
public class ConsentController {

  private final ConsentCommandService commandService;
  private final ConsentQueryService queryService;

  @Operation(summary = "약관 동의 저장", description = "사용자가 특정 약관에 동의한 내용을 저장합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<ConsentResponse>> saveConsent(
      @Parameter(description = "사용자 ID") @PathVariable Long userId,
      @RequestBody @Valid ConsentRequest request,
      @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);
    log.info("[ConsentController] 약관 동의 저장 요청: userId={}, termId={}, consented={}",
        userId, request.termId(), request.consented());

    return BaseResponse.success(commandService.saveConsent(userId, request));
  }

  @Operation(summary = "사용자 동의 목록 조회", description = "해당 사용자가 동의한 약관 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<BaseResponse<List<ConsentResponse>>> getUserConsents(
      @Parameter(description = "사용자 ID") @PathVariable Long userId,
      @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);
    log.info("[ConsentController] 사용자 약관 동의 목록 조회 요청: userId={}", userId);

    return BaseResponse.success(queryService.getUserConsents(userId));
  }

  @Operation(summary = "특정 약관 동의 여부 확인", description = "해당 사용자가 특정 약관에 동의했는지 여부를 확인합니다.")
  @GetMapping("/check/{termId}")
  public ResponseEntity<BaseResponse<Boolean>> hasConsented(
      @Parameter(description = "사용자 ID") @PathVariable Long userId,
      @Parameter(description = "약관 ID") @PathVariable Long termId,
      @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);
    log.info("[ConsentController] 동의 여부 확인 요청: userId={}, termId={}", userId, termId);

    return BaseResponse.success(queryService.hasConsented(userId, termId));
  }

  @Operation(summary = "동의 철회 (삭제)", description = "해당 사용자의 특정 약관 동의 정보를 삭제합니다.")
  @DeleteMapping("/{termId}")
  public ResponseEntity<BaseResponse<Void>> deleteConsent(
      @Parameter(description = "사용자 ID") @PathVariable Long userId,
      @Parameter(description = "약관 ID") @PathVariable Long termId,
      @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);
    log.info("[ConsentController] 약관 동의 철회 요청: userId={}, termId={}", userId, termId);

    commandService.deleteConsent(userId, termId);
    return BaseResponse.success();
  }

  private void validateUser(Long userId, UserPrincipal principal) {
    if (!userId.equals(principal.getId())) {
      log.warn("[ConsentController] 인증된 사용자와 요청된 사용자 ID 불일치: authenticatedId={}, requestedId={}",
          principal.getId(), userId);
      throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
    }
  }
}
