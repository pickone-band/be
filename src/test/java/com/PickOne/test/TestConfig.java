package com.PickOne.test;

import com.PickOne.security.filter.JwtAuthenticationFilter;
import com.PickOne.security.handler.CustomAccessDeniedHandler;
import com.PickOne.security.handler.CustomAuthenticationEntryPoint;
import com.PickOne.security.repository.TokenBlacklistRepository;
import com.PickOne.security.service.CustomUserDetailsService;
import com.PickOne.security.service.JwtService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class TestConfig {

    // Redis 관련
    @Bean
    public TokenBlacklistRepository tokenBlacklistRepository() {
        return Mockito.mock(TokenBlacklistRepository.class);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        return Mockito.mock(RedisTemplate.class);
    }

    // Security 관련
    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return Mockito.mock(CustomUserDetailsService.class);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return Mockito.mock(JwtAuthenticationFilter.class);
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return Mockito.mock(CustomAuthenticationEntryPoint.class);
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return Mockito.mock(CustomAccessDeniedHandler.class);
    }

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }
}
