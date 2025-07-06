package com.pickone.global.oauth2.controller;

import com.pickone.global.oauth2.dto.TokenResponse;
import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import com.pickone.global.oauth2.service.CustomOAuth2UserService;
import com.pickone.global.security.model.entity.UserPrincipal;
import com.pickone.global.security.service.JwtService;
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
  private final CustomOAuth2UserService customOAuth2UserService;

  @Operation(summary = "OAuth2 로그인 URL 반환", description = "프론트엔드가 사용할 수 있는 OAuth2 로그인 URL을 반환합니다.")
  @GetMapping("/url/{provider}")
  public ResponseEntity<Map<String, String>> getOAuth2LoginUrl(
      @Parameter(description = "OAuth2 제공자 (예: google, kakao, naver)") @PathVariable String provider) {
    try {
      OAuth2Provider oAuth2Provider = OAuth2Provider.valueOf(provider.toUpperCase());

      String authorizationUrl = "/oauth2/authorize/" + provider.toLowerCase();

      Map<String, String> response = new HashMap<>();
      response.put("authorizationUrl", authorizationUrl);

      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      log.error("지원하지 않는 OAuth2 제공자: {}", provider);
      return ResponseEntity.badRequest().build();
    }
  }

  @Operation(summary = "OAuth2 로그인 리다이렉트", description = "지정된 OAuth2 제공자로 리다이렉트합니다.")
  @GetMapping("/login/{provider}")
  public RedirectView redirectToOAuth2Login(
      @Parameter(description = "OAuth2 제공자") @PathVariable String provider) {
    try {
      OAuth2Provider.valueOf(provider.toUpperCase());
      String authorizationUrl = "/oauth2/authorize/" + provider.toLowerCase();
      return new RedirectView(authorizationUrl);
    } catch (IllegalArgumentException e) {
      log.error("지원하지 않는 OAuth2 제공자: {}", provider);
      return new RedirectView("/login?error=unsupported_provider");
    }
  }

  @Operation(summary = "현재 로그인 사용자 정보 조회", description = "JWT 기반 인증된 사용자에게 토큰 정보를 반환합니다.")
  @GetMapping("/user")
  public ResponseEntity<?> getCurrentUser(
      @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
    if (userPrincipal == null) {
      return ResponseEntity.status(401).build();
    }

    TokenResponse tokenResponse = new TokenResponse(
        jwtService.generateAccessToken(userPrincipal),
        jwtService.generateRefreshToken(userPrincipal),
        "Bearer",
        jwtService.getAccessTokenExpiration()
    );

    return ResponseEntity.ok(tokenResponse);
  }
}
