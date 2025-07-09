package com.pickone.global.oauth2.controller;

import com.pickone.global.oauth2.dto.TokenResponse;
import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import com.pickone.global.security.entity.UserPrincipal;
import com.pickone.global.security.service.JwtService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.SuccessCode;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OAuth2", description = "OAuth2 로그인 및 인증 관련 API")
public class OAuth2Controller {

  private final JwtService jwtService;

  @Operation(summary = "OAuth2 로그인 URL 반환", description = "프론트엔드가 사용할 수 있는 OAuth2 로그인 URL을 반환합니다.")
  @GetMapping("/url/{provider}")
  public ResponseEntity<BaseResponse<Map<String, String>>> getOAuth2LoginUrl(
      @Parameter(description = "OAuth2 제공자 (예: google, spotify, soundcloud, instagram)", example = "google")
      @PathVariable String provider) {

    try {
      OAuth2Provider oAuth2Provider = OAuth2Provider.valueOf(provider.toUpperCase());

      Map<String, String> response = new HashMap<>();
      response.put("authorizationUrl", "/oauth2/authorize/" + provider.toLowerCase());

      return BaseResponse.success(SuccessCode.OK, response);

    } catch (IllegalArgumentException e) {
      log.warn("[OAuth2Controller] 지원하지 않는 provider 요청: {}", provider);
      throw new BusinessException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
    }
  }

  @Operation(summary = "OAuth2 로그인 리다이렉트", description = "지정된 OAuth2 제공자로 리다이렉트합니다.")
  @GetMapping("/login/{provider}")
  public RedirectView redirectToOAuth2Login(
      @Parameter(description = "OAuth2 제공자", example = "google")
      @PathVariable String provider) {

    try {
      OAuth2Provider.valueOf(provider.toUpperCase());
      return new RedirectView("/oauth2/authorize/" + provider.toLowerCase());

    } catch (IllegalArgumentException e) {
      log.warn("[OAuth2Controller] 리다이렉트 실패 - 지원하지 않는 provider: {}", provider);
      return new RedirectView("/login?error=unsupported_provider");
    }
  }

  @Operation(summary = "현재 로그인 사용자 토큰 발급", description = "JWT 기반 인증된 사용자에게 토큰 정보를 반환합니다.")
  @GetMapping("/user")
  public ResponseEntity<BaseResponse<TokenResponse>> getCurrentUser(
      @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

    if (userPrincipal == null) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    TokenResponse tokenResponse = new TokenResponse(
        jwtService.generateAccessToken(userPrincipal),
        jwtService.generateRefreshToken(userPrincipal),
        "Bearer",
        jwtService.getAccessTokenExpiration()
    );

    return BaseResponse.success(SuccessCode.OK, tokenResponse);
  }
}
