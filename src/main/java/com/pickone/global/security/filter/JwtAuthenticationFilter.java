package com.pickone.global.security.filter;

import com.pickone.global.security.token.TokenProvider;
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

    final String uri = request.getRequestURI();
    final String token = tokenProvider.resolveToken(request);
    final Authentication current = SecurityContextHolder.getContext().getAuthentication();

    log.debug("[JwtAuthenticationFilter] 요청 URI: {}", uri);

    if (token != null && (current == null || current instanceof AnonymousAuthenticationToken)) {
      try {
        Authentication auth = tokenProvider.getAuthentication(token);
        if (auth instanceof UsernamePasswordAuthenticationToken authToken) {
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.info("[JwtAuthenticationFilter] 인증 성공: {}", auth.getName());
      } catch (Exception e) {
        log.warn("[JwtAuthenticationFilter] 인증 실패: {}", e.getMessage());
      }
    } else if (token == null) {
      log.debug("[JwtAuthenticationFilter] 토큰 없음");
    } else {
      log.debug("[JwtAuthenticationFilter] 이미 인증된 사용자: {}", current.getName());
    }

    filterChain.doFilter(request, response);
  }
}