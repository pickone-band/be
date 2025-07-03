package com.pickone.global.security.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailTokenService {
  private final StringRedisTemplate redisTemplate;
  private static final long TOKEN_EXPIRATION_SECONDS = 86400; // 1일

  // 토큰 생성 및 Redis 저장
  public String createAndSaveToken(String email) {
    String token = UUID.randomUUID().toString();
    redisTemplate.opsForValue().set(token, email, TOKEN_EXPIRATION_SECONDS, TimeUnit.SECONDS);
    return token;
  }

  // 토큰으로 이메일 조회 (검증)
  public Optional<String> validateTokenAndGetEmail(String token) {
    String email = redisTemplate.opsForValue().get(token);
    if (email == null || email.isEmpty()) {
      return Optional.empty();
    }
    // 토큰 사용 후 삭제
    redisTemplate.delete(token);
    return Optional.of(email);
  }

  public String getEmailByToken(String token) {
    return redisTemplate.opsForValue().get(token);
  }

  public void deleteToken(String token) {
    redisTemplate.delete(token);
  }
}
