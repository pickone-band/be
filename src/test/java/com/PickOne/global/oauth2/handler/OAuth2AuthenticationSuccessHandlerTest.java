package com.PickOne.global.oauth2.handler;

import com.PickOne.global.security.model.entity.SecurityUser;
import com.PickOne.global.security.service.JwtService;
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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private OAuth2AuthenticationToken authentication;

    @Mock
    private RedirectStrategy redirectStrategy;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler successHandler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(successHandler, "defaultRedirectUri", "http://localhost:3000/oauth2/redirect");
        ReflectionTestUtils.setField(successHandler, "redirectStrategy", redirectStrategy);
    }

    @Test
    @DisplayName("인증 성공 시 리다이렉트 - 응답이 이미 커밋된 경우")
    void onAuthenticationSuccess_ResponseAlreadyCommitted() throws IOException, ServletException {
        // given
        when(response.isCommitted()).thenReturn(true);

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(redirectStrategy, never()).sendRedirect(any(), any(), anyString());
    }

    @Test
    @DisplayName("인증 성공 시 리다이렉트 - 정상 케이스")
    void onAuthenticationSuccess_Success() throws IOException, ServletException {
        // given
        when(response.isCommitted()).thenReturn(false);

        SecurityUser securityUser = mock(SecurityUser.class);
        OAuth2User oAuth2User = mock(OAuth2User.class);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("securityUser", securityUser);

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(attributes);

        when(jwtService.generateAccessToken(securityUser)).thenReturn("access_token");
        when(jwtService.generateRefreshToken(securityUser)).thenReturn("refresh_token");

        when(request.getParameter("redirect_uri")).thenReturn("http://test.com/callback");

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(redirectStrategy).sendRedirect(eq(request), eq(response),
                contains("http://test.com/callback?token=access_token&refresh_token=refresh_token"));
    }

    @Test
    @DisplayName("대상 URL 결정 - 사용자 정보가 없는 경우")
    void determineTargetUrl_UserNotFound() {
        // given
        OAuth2User oAuth2User = mock(OAuth2User.class);

        Map<String, Object> attributes = new HashMap<>();
        // securityUser가 없는 경우

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(attributes);

        when(request.getParameter("redirect_uri")).thenReturn(null);

        // when
        String targetUrl = successHandler.determineTargetUrl(request, response, authentication);

        // then
        assertEquals("http://localhost:3000/oauth2/redirect?error=user_not_found", targetUrl);
    }
}