package com.example.shoppingmallproject.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Refresh Token 저장
     */
    public void saveRefreshToken(String userId, String refreshToken, Duration duration) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(userId, refreshToken, duration);
        log.info("Saved data - Key: {}, Value: {}", userId, refreshToken);
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(String userId) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        log.info("Retrieved data - Key: {}, Value: {}", userId, valueOperations.get(userId)); // 조회한 값 로그로 출력
        return (String) valueOperations.get(userId);
    }

    // refreshToken을 키로 하여 Redis에서 userId를 가져오는 메서드
    public String getUserIdByRefreshToken(String refreshToken) {
        return (String) redisTemplate.opsForValue().get(refreshToken); // refreshToken으로 userId 조회
    }

    /**
     * Refresh Token 삭제
     */
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(userId);
    }
}
