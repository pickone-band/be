package com.PickOne.global.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationFailureHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException exception;

    @Mock
    private RedirectStrategy redirectStrategy;

    @InjectMocks
    private OAuth2AuthenticationFailureHandler failureHandler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(failureHandler, "defaultRedirectUri", "http://localhost:3000/oauth2/redirect");
        ReflectionTestUtils.setField(failureHandler, "redirectStrategy", redirectStrategy);
    }

    @Test
    @DisplayName("인증 실패 시 기본 리다이렉트 URI 사용")
    void onAuthenticationFailure_DefaultRedirectUri() throws IOException, ServletException {
        // given
        when(request.getParameter("redirect_uri")).thenReturn(null);
        when(exception.getMessage()).thenReturn("인증 실패");

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        verify(redirectStrategy).sendRedirect(eq(request), eq(response),
                eq("http://localhost:3000/oauth2/redirect?error=인증 실패"));
    }

    @Test
    @DisplayName("인증 실패 시 요청된 리다이렉트 URI 사용")
    void onAuthenticationFailure_RequestRedirectUri() throws IOException, ServletException {
        // given
        when(request.getParameter("redirect_uri")).thenReturn("http://test.com/callback");
        when(exception.getMessage()).thenReturn("인증 실패");

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        verify(redirectStrategy).sendRedirect(eq(request), eq(response),
                eq("http://test.com/callback?error=인증 실패"));
    }
}