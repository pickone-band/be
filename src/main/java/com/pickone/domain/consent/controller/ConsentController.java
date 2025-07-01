package com.pickone.domain.consent.controller;

import com.pickone.domain.consent.dto.ConsentResponseDto;
import com.pickone.domain.consent.dto.ConsentRequestDto;
import com.pickone.domain.consent.service.ConsentCommandService;
import com.pickone.domain.consent.service.ConsentQueryService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.security.model.entity.UserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/consents")
@Tag(name = "Consent API", description = "사용자 약관 동의 API")
public class ConsentController {

  private final ConsentCommandService commandService;
  private final ConsentQueryService queryService;

  @PostMapping
  public ResponseEntity<BaseResponse<ConsentResponseDto>> saveConsent(
      @PathVariable Long userId,
      @RequestBody @Valid ConsentRequestDto request,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);
    return BaseResponse.success(commandService.saveConsent(userId, request));
  }

  @GetMapping
  public ResponseEntity<BaseResponse<List<ConsentResponseDto>>> getUserConsents(
      @PathVariable Long userId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);
    return BaseResponse.success(queryService.getUserConsents(userId));
  }

  @GetMapping("/check/{termId}")
  public ResponseEntity<BaseResponse<Boolean>> hasConsented(
      @PathVariable Long userId,
      @PathVariable Long termId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);
    return BaseResponse.success(queryService.hasConsented(userId, termId));
  }

  @DeleteMapping("/{termId}")
  public ResponseEntity<BaseResponse<Void>> deleteConsent(
      @PathVariable Long userId,
      @PathVariable Long termId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    validateUser(userId, userPrincipal);
    commandService.deleteConsent(userId, termId);
    return BaseResponse.success();
  }

  private void validateUser(Long userId, UserPrincipal principal) {
    if (!userId.equals(principal.getId())) {
      throw new RuntimeException("접근 거부");
    }
  }
}
