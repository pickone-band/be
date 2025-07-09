package com.pickone.global.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTokenService {

  private final StringRedisTemplate redis;
  private static final long EXPIRE_SECONDS = 86400; // 1일

  public String createAndSaveToken(String email) {
    String token = UUID.randomUUID().toString();
    redis.opsForValue().set(token, email, EXPIRE_SECONDS, TimeUnit.SECONDS);
    log.debug("[EmailTokenService] 토큰 생성: email={}, token={}", email, token);
    return token;
  }

  public Optional<String> validateTokenAndGetEmail(String token) {
    String email = redis.opsForValue().get(token);
    if (email == null || email.isBlank()) {
      log.warn("[EmailTokenService] 유효하지 않은 토큰: {}", token);
      return Optional.empty();
    }
    redis.delete(token);
    return Optional.of(email);
  }

  public void deleteToken(String token) {
    redis.delete(token);
    log.debug("[EmailTokenService] 토큰 삭제됨: {}", token);
  }

  public String getEmailByToken(String token) {
    return redis.opsForValue().get(token);
  }
}
