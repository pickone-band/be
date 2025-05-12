package com.PickOne.global.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisConfigTest{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic messageTopic;

    @Autowired
    private ChannelTopic notificationTopic;

    @Test
    public void testRedisConnection() {
        assertNotNull(redisTemplate);
        assertNotNull(messageTopic);
        assertNotNull(notificationTopic);

        // Redis에 간단한 테스트 메시지 발행
        String testKey = "test:connection";
        redisTemplate.opsForValue().set(testKey, "Connected!");
        String value = (String) redisTemplate.opsForValue().get(testKey);

        assertNotNull(value);
        System.out.println("Redis connection test: " + value);

        // 테스트 후 키 삭제
        redisTemplate.delete(testKey);
    }
}