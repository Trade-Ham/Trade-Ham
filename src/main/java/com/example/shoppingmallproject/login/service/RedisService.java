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
    public void saveRefreshToken(String userEmail, String refreshToken, Duration duration) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(userEmail, refreshToken, duration);
        log.info("Saved data - Key: {}, Value: {}", userEmail, refreshToken);
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(String userEmail) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        log.info("Retrieved data - Key: {}, Value: {}", userEmail, (String) valueOperations.get(userEmail)); // 조회한 값 로그로 출력
        return (String) valueOperations.get(userEmail);
    }

    /**
     * Refresh Token 삭제
     */
    public void deleteRefreshToken(String userEmail) {
        redisTemplate.delete(userEmail);
    }
}
