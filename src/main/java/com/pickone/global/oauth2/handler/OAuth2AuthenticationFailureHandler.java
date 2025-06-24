package com.pickone.global.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Value("${app.oauth2.redirect-uri:http://localhost:3000/oauth2/redirect}")
  private String defaultRedirectUri;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {

    log.error("OAuth2 인증 실패: {}", exception.getMessage());

    String redirectUri = request.getParameter("redirect_uri");

    if (redirectUri == null || redirectUri.isBlank()) {
      redirectUri = defaultRedirectUri;
    }

    String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
        .queryParam("error", exception.getMessage())
        .build().toUriString();

    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }
}