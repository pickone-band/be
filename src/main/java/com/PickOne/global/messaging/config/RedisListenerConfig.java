package com.PickOne.global.messaging.config;

import com.PickOne.global.websocket.handler.RedisMessageSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

    private final RedisMessageSubscriber redisMessageSubscriber;
    private final ChannelTopic messageTopic;
    private final ChannelTopic notificationTopic;

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        // Redis 메시지 구독자를 위한 어댑터 생성
        return new MessageListenerAdapter(redisMessageSubscriber);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        // Redis 메시지 리스너 컨테이너 설정
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 두 토픽에 대한 리스너 등록
        container.addMessageListener(messageListenerAdapter(), messageTopic);
        container.addMessageListener(messageListenerAdapter(), notificationTopic);

        return container;
    }
}