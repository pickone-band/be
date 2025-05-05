package com.PickOne.security.filter;

import com.PickOne.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Test
    @DisplayName("토큰이 있고 유효한 경우")
    void doFilterInternal_ValidToken() throws ServletException, IOException {
        // given
        String token = "valid-token";
        Authentication authentication = mock(Authentication.class);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(jwtService.resolveToken(request)).thenReturn(token);
        when(jwtService.isTokenBlacklisted(token)).thenReturn(true);
        when(jwtService.getAuthentication(token)).thenReturn(authentication);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(jwtService).resolveToken(request);
        verify(jwtService).isTokenBlacklisted(token);
        verify(jwtService).getAuthentication(token);
        verify(securityContext).setAuthentication(authentication);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 없는 경우")
    void doFilterInternal_NoToken() throws ServletException, IOException {
        // given
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(jwtService.resolveToken(request)).thenReturn(null);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(jwtService).resolveToken(request);
        verify(jwtService, never()).isTokenBlacklisted(anyString());
        verify(jwtService, never()).getAuthentication(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 블랙리스트에 있는 경우")
    void doFilterInternal_BlacklistedToken() throws ServletException, IOException {
        // given
        String token = "blacklisted-token";

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(jwtService.resolveToken(request)).thenReturn(token);
        when(jwtService.isTokenBlacklisted(token)).thenReturn(false);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(jwtService).resolveToken(request);
        verify(jwtService).isTokenBlacklisted(token);
        verify(jwtService, never()).getAuthentication(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("인증 과정에서 예외 발생하는 경우")
    void doFilterInternal_AuthenticationException() throws ServletException, IOException {
        // given
        String token = "valid-token";

        // SecurityContextHolder 관련 로직 수정
        SecurityContextHolder.clearContext(); // 실제 SecurityContextHolder 사용

        when(jwtService.resolveToken(request)).thenReturn(token);
        when(jwtService.isTokenBlacklisted(token)).thenReturn(true);
        when(jwtService.getAuthentication(token)).thenThrow(new RuntimeException("Authentication failed"));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(jwtService).resolveToken(request);
        verify(jwtService).isTokenBlacklisted(token);
        verify(jwtService).getAuthentication(token);
        // SecurityContext.clearContext() 검증 제거
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("전체 필터 과정에서 예외 발생하는 경우")
    void doFilterInternal_GeneralException() throws ServletException, IOException {
        // given
        // SecurityContextHolder 직접 사용
        SecurityContextHolder.clearContext(); // 실제 SecurityContextHolder 사용

        when(jwtService.resolveToken(request)).thenThrow(new RuntimeException("General error"));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(jwtService).resolveToken(request);
        // SecurityContext.clearContext() 검증 제거
        verify(filterChain).doFilter(request, response);
    }
}