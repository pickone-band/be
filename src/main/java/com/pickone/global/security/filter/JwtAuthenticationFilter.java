package com.pickone.global.security.filter;

import com.pickone.global.security.service.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenProvider tokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestURI = request.getRequestURI();
    String token = tokenProvider.resolveToken(request);
    Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

    log.debug("요청 URI: {}", requestURI);

    if (token != null && (currentAuth == null
        || currentAuth instanceof AnonymousAuthenticationToken)) {
      try {
        log.debug("JWT 토큰 발견, 인증 처리 시도");
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (authentication instanceof UsernamePasswordAuthenticationToken usernameToken) {
          usernameToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(usernameToken);
        }

        log.info("JWT 인증 성공: 사용자 이메일={}", authentication.getName());

      } catch (Exception e) {
        log.warn("JWT 인증 실패: {}", e.getMessage());
      }
    } else {
      if (token == null) {
        log.debug("JWT 토큰 없음");
      } else {
        log.debug("이미 인증된 상태, 토큰 재인증 생략: {}", currentAuth.getName());
      }
    }

    filterChain.doFilter(request, response);
  }
}
