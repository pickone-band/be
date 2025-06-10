package com.PickOne.global.security.repository.impl;

import com.PickOne.global.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String email, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set(email, refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public String find(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    @Override
    public void delete(String email) {
        redisTemplate.delete(email);
    }
}