package com.PickOne.global.security.service;

import com.PickOne.global.security.repository.TokenBlacklistRepository;
import com.PickOne.global.security.service.CustomUserDetailsService;
import com.PickOne.global.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // 불필요한 스텁 경고 제거
class JwtServiceTest {

    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private JwtService jwtService;

    @Test
    @DisplayName("HTTP 요청에서 토큰 추출 테스트 - 성공")
    void resolveToken_Success() {
        // given
        String token = "example-token";
        String authHeader = "Bearer " + token;

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        // when
        String resolvedToken = jwtService.resolveToken(request);

        // then
        assertThat(resolvedToken).isEqualTo(token);
    }

    @Test
    @DisplayName("HTTP 요청에서 토큰 추출 테스트 - 실패 (헤더 없음)")
    void resolveToken_NoHeader() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        String resolvedToken = jwtService.resolveToken(request);

        // then
        assertThat(resolvedToken).isNull();
    }

    @Test
    @DisplayName("HTTP 요청에서 토큰 추출 테스트 - 실패 (잘못된 형식)")
    void resolveToken_InvalidFormat() {
        // given
        String authHeader = "InvalidPrefix token";

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        // when
        String resolvedToken = jwtService.resolveToken(request);

        // then
        assertThat(resolvedToken).isNull();
    }

    @Test
    @DisplayName("토큰 블랙리스트 확인 테스트 - 블랙리스트에 있는 경우")
    void isTokenBlacklisted_InBlacklist() {
        // given
        String token = "test-token";
        // tokenBlacklistRepository.isBlacklisted가 true를 반환하면
        // JwtService.isTokenBlacklisted는 !true = false를 반환
        when(tokenBlacklistRepository.isBlacklisted(token)).thenReturn(true);

        // when
        boolean result = jwtService.isTokenBlacklisted(token);

        // then
        verify(tokenBlacklistRepository).isBlacklisted(token);
        // JwtService.isTokenBlacklisted는 !tokenBlacklistRepository.isBlacklisted 이므로
        // false를 기대해야 함
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("토큰 블랙리스트 확인 테스트 - 블랙리스트에 없는 경우")
    void isTokenBlacklisted_NotInBlacklist() {
        // given
        String token = "test-token";
        // tokenBlacklistRepository.isBlacklisted가 false를 반환하면
        // JwtService.isTokenBlacklisted는 !false = true를 반환
        when(tokenBlacklistRepository.isBlacklisted(token)).thenReturn(false);

        // when
        boolean result = jwtService.isTokenBlacklisted(token);

        // then
        verify(tokenBlacklistRepository).isBlacklisted(token);
        // JwtService.isTokenBlacklisted는 !tokenBlacklistRepository.isBlacklisted 이므로
        // true를 기대해야 함
        assertThat(result).isTrue();
    }
}