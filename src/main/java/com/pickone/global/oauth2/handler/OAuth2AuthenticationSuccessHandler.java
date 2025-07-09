package com.pickone.global.oauth2.handler;

import com.pickone.global.security.entity.UserPrincipal;
import com.pickone.global.security.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtService jwtService;

  @Value("${app.oauth2.redirect-uri:http://localhost:3000/oauth2/redirect}")
  private String defaultRedirectUri;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    if (response.isCommitted()) {
      log.warn("응답이 이미 커밋되었습니다. OAuth2 인증 성공 핸들러를 실행할 수 없습니다.");
      return;
    }

    String targetUrl = determineTargetUrl(request, authentication);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  protected String determineTargetUrl(HttpServletRequest request, Authentication authentication) {
    String redirectUri = request.getParameter("redirect_uri");
    if (redirectUri == null || redirectUri.isBlank()) {
      redirectUri = defaultRedirectUri;
    }

    if (!(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
      log.error("OAuth2 인증 객체가 UserPrincipal이 아님");
      return redirectUri + "?error=unauthorized";
    }

    String accessToken = jwtService.generateAccessToken(userPrincipal);
    String refreshToken = jwtService.generateRefreshToken(userPrincipal);

    return UriComponentsBuilder.fromUriString(redirectUri)
        .queryParam("token", accessToken)
        .queryParam("refresh_token", refreshToken)
        .build().toUriString();
  }
}
