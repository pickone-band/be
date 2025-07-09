package com.pickone.global.security.service;

import com.pickone.global.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

  private final StringRedisTemplate redis;

  @Override
  public void save(String email, String token, long ttlMillis) {
    redis.opsForValue().set(token, email, Duration.ofMillis(ttlMillis));
    log.debug("[RedisRefreshTokenRepo] 저장됨: email={}, ttl={}ms", email, ttlMillis);
  }

  @Override
  public boolean existsByToken(String token) {
    return redis.hasKey(token);
  }

  @Override
  public void deleteByToken(String token) {
    redis.delete(token);
    log.debug("[RedisRefreshTokenRepo] 삭제됨: token={}", token);
  }

  @Override
  public String findUserEmailByToken(String token) {
    return redis.opsForValue().get(token);
  }
}
