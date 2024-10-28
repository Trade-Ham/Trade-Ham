package com.trade_ham.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisRefreshService {

    private final RedisTemplate<String, String> redisTemplate;

    // 리프레시 토큰 저장 (TTL 설정)
    public void saveRefreshToken(String userName, String refreshToken, long expirationTimeInMillis) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(userName, refreshToken, expirationTimeInMillis, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String userName) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(userName);
    }

    public void deleteRefreshToken(String userName) {
        log.info(userName);
        redisTemplate.delete(userName);
    }

    public boolean hasValidRefreshToken(String userName, String refreshToken) {
        String storedToken = getRefreshToken(userName);
        return storedToken != null && storedToken.equals(refreshToken);
    }

// 밀로코드
//    // 리프레시 토큰 저장 (TTL 설정)
//    public void saveRefreshToken(Long userId, String refreshToken, long expirationTimeInMillis) {
//        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//        valueOperations.set(String.valueOf(userId), refreshToken, expirationTimeInMillis, TimeUnit.MILLISECONDS);
//    }
//
//    public String getRefreshToken(Long userId) {
//        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//        return valueOperations.get(String.valueOf(userId));
//    }
//
//    public void deleteRefreshToken(Long userId) {
//        log.info(String.valueOf(userId));
//        redisTemplate.delete(String.valueOf(userId));
//    }
//
//    public boolean hasValidRefreshToken(Long userId, String refreshToken) {
//        String storedToken = getRefreshToken(userId);
//        return storedToken != null && storedToken.equals(refreshToken);
//    }

}
