package com.trade_ham.domain.product.service;

import com.trade_ham.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class LikeSyncService {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final ProductRepository productRepository;

    private static final String REDIS_KEY_PREFIX = "product:likes:";

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void syncLikesToDatabase() {
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                Integer likeCount = redisTemplate.opsForValue().get(key);
                if (likeCount != null) {
                    Long productId = extractProductIdFromKey(key);
                    productRepository.findById(productId).ifPresent(product -> {
                        product.setLikeCount(likeCount); // DB에 동기화
                        productRepository.save(product);
                    });
                }
            }
        }
    }

    private Long extractProductIdFromKey(String key) {
        return Long.parseLong(key.replace(REDIS_KEY_PREFIX, ""));
    }
}
