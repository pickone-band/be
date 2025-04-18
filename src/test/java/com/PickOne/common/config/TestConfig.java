package com.PickOne.common.config;

import com.PickOne.user.service.PasswordEncoder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestConfig {

    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        // 실제 BCrypt 인코더 사용
        BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

        return new PasswordEncoder() {
            @Override
            public String encode(String rawPassword) {
                return delegate.encode(rawPassword);
            }

            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return delegate.matches(rawPassword, encodedPassword);
            }
        };
    }


}