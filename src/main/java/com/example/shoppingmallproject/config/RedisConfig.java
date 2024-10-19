package com.example.shoppingmallproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    // RedisConnectionFactory 설정: Redis 서버와 연결하기 위한 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);  // Redis 서버의 호스트와 포트 설정
    }

    // RedisTemplate 설정: Redis와 상호작용하는 데 사용
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // key와 value의 직렬화 방식 설정
        template.setKeySerializer(new StringRedisSerializer());    // key를 String으로 직렬화
        template.setValueSerializer(new StringRedisSerializer());  // value를 String으로 직렬화

        return template;
    }
}