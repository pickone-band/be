package com.pickone.domain.consent.controller;

import com.pickone.domain.consent.dto.ConsentResponseDto;
import com.pickone.domain.consent.dto.ConsentTermtDto;
import com.pickone.domain.consent.model.entity.ConsentEntity;
import com.pickone.domain.consent.service.ConsentService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.security.model.entity.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users/{userId}/consents")
@Tag(name = "Consent API", description = "사용자 약관 동의 API")
public class ConsentController {

  private final ConsentService consentService;

  @Operation(summary = "동의 저장", description = "사용자의 약관 동의 여부를 저장합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<ConsentResponseDto>> saveConsent(
      @PathVariable Long userId,
      @RequestBody @Valid ConsentTermtDto request,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);

    log.info("약관 동의 저장 요청: userId={}, termId={}, consented={}",
        userId, request.termId(), request.consented());

    ConsentEntity saved = consentService.saveConsent(userId, request.termId(), request.consented());
    return BaseResponse.success(ConsentResponseDto.from(saved));
  }

  @Operation(summary = "동의 목록 조회", description = "특정 사용자의 전체 약관 동의 내역을 조회합니다.")
  @GetMapping
  public ResponseEntity<BaseResponse<List<ConsentResponseDto>>> getUserConsents(
      @PathVariable Long userId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);

    log.info("사용자 약관 동의 목록 조회 요청: userId={}", userId);
    List<ConsentResponseDto> responses = consentService.getUserConsents(userId).stream()
        .map(ConsentResponseDto::from)
        .toList();
    return BaseResponse.success(responses);
  }

  @Operation(summary = "특정 약관 동의 여부 확인", description = "특정 약관에 대한 사용자의 동의 여부를 확인합니다.")
  @GetMapping("/check/{termId}")
  public ResponseEntity<BaseResponse<Boolean>> hasConsented(
      @PathVariable Long userId,
      @PathVariable Long termId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);

    log.info("동의 여부 확인 요청: userId={}, termId={}", userId, termId);
    boolean result = consentService.hasConsented(userId, termId);
    return BaseResponse.success(result);
  }

  @Operation(summary = "동의 삭제", description = "사용자의 특정 약관 동의 정보를 삭제합니다.")
  @DeleteMapping("/{termId}")
  public ResponseEntity<BaseResponse<Void>> deleteConsent(
      @PathVariable Long userId,
      @PathVariable Long termId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);

    log.info("약관 동의 삭제 요청: userId={}, termId={}", userId, termId);
    consentService.deleteConsent(userId, termId);
    return BaseResponse.success();
  }

  private void validateUser(Long userId, UserPrincipal principal) {
    if (!userId.equals(principal.getUserId())) {
      log.warn("접근 거부: 인증된 사용자 ID {}와 요청 ID {} 불일치", principal.getUserId(), userId);
      throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
    }
  }
}
