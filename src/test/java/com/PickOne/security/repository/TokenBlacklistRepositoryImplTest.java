package com.PickOne.security.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistRepositoryImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenBlacklistRepositoryImpl tokenBlacklistRepository;

    @Test
    @DisplayName("토큰 블랙리스트 추가 테스트 - TTL 양수")
    void addToBlacklist_PositiveTtl() {
        // given
        String token = "test-token";
        long ttlMillis = 3600000; // 1 hour

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        // when
        tokenBlacklistRepository.addToBlacklist(token, ttlMillis);

        // then
        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(eq("blacklist:" + token), eq("blacklisted"), eq(ttlMillis), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("토큰 블랙리스트 추가 테스트 - TTL 0 이하")
    void addToBlacklist_NonPositiveTtl() {
        // given
        String token = "test-token";
        long ttlMillis = 0;

        // when
        tokenBlacklistRepository.addToBlacklist(token, ttlMillis);

        // then
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("토큰 블랙리스트 확인 테스트 - 블랙리스트에 있음")
    void isBlacklisted_True() {
        // given
        String token = "test-token";

        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        // when
        boolean result = tokenBlacklistRepository.isBlacklisted(token);

        // then
        verify(redisTemplate).hasKey(eq("blacklist:" + token));
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("토큰 블랙리스트 확인 테스트 - 블랙리스트에 없음")
    void isBlacklisted_False() {
        // given
        String token = "test-token";

        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // when
        boolean result = tokenBlacklistRepository.isBlacklisted(token);

        // then
        verify(redisTemplate).hasKey(eq("blacklist:" + token));
        assertThat(result).isFalse();
    }
}