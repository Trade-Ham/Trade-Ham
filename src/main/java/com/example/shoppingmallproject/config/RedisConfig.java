package com.example.shoppingmallproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Key와 Value를 각각의 Serializer로 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer()); //Key는 문자열로 직렬화(저장)
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // Value는 JSON 형식으로 직렬화(저장)
        
        return redisTemplate;
    }
}
