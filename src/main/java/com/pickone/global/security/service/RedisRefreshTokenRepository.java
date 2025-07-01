package com.pickone.global.security.service;

import com.pickone.global.security.repository.RefreshTokenRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {
  private final StringRedisTemplate redisTemplate;

  @Override
  public void save(String userEmail, String refreshToken, long ttlMillis) {
    redisTemplate.opsForValue().set(refreshToken, userEmail, Duration.ofMillis(ttlMillis));
  }

  @Override
  public boolean existsByToken(String refreshToken) {
    return redisTemplate.hasKey(refreshToken);
  }

  @Override
  public void deleteByToken(String refreshToken) {
    redisTemplate.delete(refreshToken);
  }

  @Override
  public String findUserEmailByToken(String refreshToken) {
    return redisTemplate.opsForValue().get(refreshToken);
  }
}
