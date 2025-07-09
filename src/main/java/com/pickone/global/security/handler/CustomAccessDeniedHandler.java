package com.pickone.global.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException ex) throws IOException {
    log.warn("[AccessDeniedHandler] 접근 거부: URI={}, 사유={}", request.getRequestURI(), ex.getMessage());
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write("{\"message\": \"Forbidden\"}");
  }
}
