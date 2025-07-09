package com.pickone.global.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailTokenServiceTest {

  private StringRedisTemplate redisTemplate;
  private ValueOperations<String, String> valueOps;
  private EmailTokenService service;

  @BeforeEach
  void setUp() {
    redisTemplate = mock(StringRedisTemplate.class);
    valueOps = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);

    service = new EmailTokenService(redisTemplate);
  }

  @Test
  void createAndSaveToken_shouldStoreTokenAndReturnIt() {
    // given
    String email = "test@example.com";

    // when
    String token = service.createAndSaveToken(email);

    // then
    assertNotNull(token);
    verify(valueOps).set(eq(token), eq(email), eq(86400L), eq(TimeUnit.SECONDS));
  }

  @Test
  void validateTokenAndGetEmail_validToken_shouldReturnEmailAndDeleteToken() {
    // given
    String token = UUID.randomUUID().toString();
    String email = "user@example.com";

    when(valueOps.get(token)).thenReturn(email);

    // when
    Optional<String> result = service.validateTokenAndGetEmail(token);

    // then
    assertTrue(result.isPresent());
    assertEquals(email, result.get());
    verify(redisTemplate).delete(token);
  }

  @Test
  void validateTokenAndGetEmail_invalidToken_shouldReturnEmpty() {
    // given
    String token = UUID.randomUUID().toString();

    when(valueOps.get(token)).thenReturn(null);

    // when
    Optional<String> result = service.validateTokenAndGetEmail(token);

    // then
    assertTrue(result.isEmpty());
    verify(redisTemplate, never()).delete(token);
  }

  @Test
  void deleteToken_shouldRemoveFromRedis() {
    // given
    String token = UUID.randomUUID().toString();

    // when
    service.deleteToken(token);

    // then
    verify(redisTemplate).delete(token);
  }

  @Test
  void getEmailByToken_shouldReturnStoredEmail() {
    // given
    String token = "sample-token";
    String email = "test@example.com";
    when(valueOps.get(token)).thenReturn(email);

    // when
    String result = service.getEmailByToken(token);

    // then
    assertEquals(email, result);
  }
}
