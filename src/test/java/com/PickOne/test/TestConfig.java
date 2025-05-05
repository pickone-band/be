package com.PickOne.test;

import com.PickOne.security.repository.TokenBlacklistRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class TestConfig {
    @Bean
    public TokenBlacklistRepository tokenBlacklistRepository() {
        return Mockito.mock(TokenBlacklistRepository.class);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        return Mockito.mock(RedisTemplate.class);
    }
}