package com.pickone.global.security.service;

import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {

  @Mock
  private StringRedisTemplate redisTemplate;

  @Mock
  private ValueOperations<String, String> valueOperations;

  @InjectMocks
  private PasswordResetTokenService tokenService;

  @BeforeEach
  void setup() {
    lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  void createAndSaveToken_shouldStoreTokenInRedis() {
    String email = "test@example.com";

    String token = tokenService.createAndSaveToken(email);

    assertNotNull(token);
    verify(valueOperations).set(eq(token), eq(email), eq(3600L), eq(TimeUnit.SECONDS));
  }

  @Test
  void validateTokenAndGetEmail_validToken_shouldReturnEmail() {
    String token = "valid-token";
    String email = "user@example.com";

    when(valueOperations.get(token)).thenReturn(email);

    String result = tokenService.validateTokenAndGetEmail(token);
    assertEquals(email, result);
  }

  @Test
  void validateTokenAndGetEmail_invalidToken_shouldThrowException() {
    String token = "invalid-token";
    when(valueOperations.get(token)).thenReturn(null);

    BusinessException exception = assertThrows(BusinessException.class,
        () -> tokenService.validateTokenAndGetEmail(token));

    assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
  }

  @Test
  void isValid_shouldReturnTrueWhenTokenExists() {
    when(redisTemplate.hasKey("token123")).thenReturn(true);
    assertTrue(tokenService.isValid("token123"));
  }

  @Test
  void isValid_shouldReturnFalseWhenTokenDoesNotExist() {
    when(redisTemplate.hasKey("token456")).thenReturn(false);
    assertFalse(tokenService.isValid("token456"));
  }

  @Test
  void deleteToken_shouldCallRedisDelete() {
    tokenService.deleteToken("token789");
    verify(redisTemplate).delete("token789");
  }
}
