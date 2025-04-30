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

    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Test
    @DisplayName("토큰을 블랙리스트에 추가한다")
    void addToBlacklist_ShouldAddTokenToBlacklist() {
        // given
        long ttlMillis = 3600000; // 1시간
        String expectedKey = BLACKLIST_PREFIX + TEST_TOKEN;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        tokenBlacklistRepository.addToBlacklist(TEST_TOKEN, ttlMillis);

        // then
        verify(valueOperations).set(eq(expectedKey), eq("blacklisted"), eq(ttlMillis), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("TTL이 0 이하인 경우 블랙리스트에 추가하지 않는다")
    void addToBlacklist_ShouldNotAddToken_WhenTtlIsZeroOrNegative() {
        // given
        long ttlMillis = 0;

        // when
        tokenBlacklistRepository.addToBlacklist(TEST_TOKEN, ttlMillis);

        // then
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("블랙리스트에 있는 토큰을 확인한다")
    void isBlacklisted_ShouldReturnTrue_WhenTokenIsBlacklisted() {
        // given
        String expectedKey = BLACKLIST_PREFIX + TEST_TOKEN;

        when(redisTemplate.hasKey(expectedKey)).thenReturn(true);

        // when
        boolean result = tokenBlacklistRepository.isBlacklisted(TEST_TOKEN);

        // then
        assertThat(result).isTrue();
        verify(redisTemplate).hasKey(expectedKey);
    }

    @Test
    @DisplayName("블랙리스트에 없는 토큰을 확인한다")
    void isBlacklisted_ShouldReturnFalse_WhenTokenIsNotBlacklisted() {
        // given
        String expectedKey = BLACKLIST_PREFIX + TEST_TOKEN;

        when(redisTemplate.hasKey(expectedKey)).thenReturn(false);

        // when
        boolean result = tokenBlacklistRepository.isBlacklisted(TEST_TOKEN);

        // then
        assertThat(result).isFalse();
        verify(redisTemplate).hasKey(expectedKey);
    }

    @Test
    @DisplayName("hasKey가 null을 반환할 경우 블랙리스트에 없는 것으로 간주한다")
    void isBlacklisted_ShouldReturnFalse_WhenHasKeyReturnsNull() {
        // given
        String expectedKey = BLACKLIST_PREFIX + TEST_TOKEN;

        when(redisTemplate.hasKey(expectedKey)).thenReturn(null);

        // when
        boolean result = tokenBlacklistRepository.isBlacklisted(TEST_TOKEN);

        // then
        assertThat(result).isFalse();
        verify(redisTemplate).hasKey(expectedKey);
    }
}