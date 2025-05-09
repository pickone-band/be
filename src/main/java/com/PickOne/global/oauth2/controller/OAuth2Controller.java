package com.PickOne.global.oauth2.controller;

import com.PickOne.global.oauth2.model.domain.OAuth2Provider;
import com.PickOne.global.oauth2.service.CustomOAuth2UserService;
import com.PickOne.global.security.dto.TokenResponse;
import com.PickOne.global.security.model.entity.SecurityUser;

import com.PickOne.global.security.service.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {

    private final JwtService jwtService;
    private final CustomOAuth2UserService customOAuth2UserService;

    /**
     * OAuth2 로그인 URL 생성
     */
    @GetMapping("/url/{provider}")
    public ResponseEntity<Map<String, String>> getOAuth2LoginUrl(@PathVariable String provider) {
        try {
            OAuth2Provider oAuth2Provider = OAuth2Provider.valueOf(provider.toUpperCase());

            // Spring Security의 OAuth2 로그인 URL
            String authorizationUrl = "/oauth2/authorize/" + provider.toLowerCase();

            Map<String, String> response = new HashMap<>();
            response.put("authorizationUrl", authorizationUrl);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("지원하지 않는 OAuth2 제공자: {}", provider);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * OAuth2 로그인 리다이렉트
     */
    @GetMapping("/login/{provider}")
    public RedirectView redirectToOAuth2Login(@PathVariable String provider) {
        try {
            // Spring Security의 OAuth2 로그인 URL로 리다이렉트
            String authorizationUrl = "/oauth2/authorize/" + provider.toLowerCase();
            return new RedirectView(authorizationUrl);
        } catch (IllegalArgumentException e) {
            log.error("지원하지 않는 OAuth2 제공자: {}", provider);
            return new RedirectView("/login?error=unsupported_provider");
        }
    }

    /**
     * 현재 인증된 사용자 정보 조회
     */
    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null) {
            return ResponseEntity.status(401).build();
        }

        TokenResponse tokenResponse = new TokenResponse(
                jwtService.generateAccessToken(securityUser),
                jwtService.generateRefreshToken(securityUser),
                "Bearer",
                jwtService.getAccessTokenExpiration()
        );

        return ResponseEntity.ok(tokenResponse);
    }
}