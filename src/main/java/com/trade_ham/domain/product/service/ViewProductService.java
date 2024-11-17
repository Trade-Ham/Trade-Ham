package com.trade_ham.domain.product.service;

import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.repository.ProductRepository;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ViewProductService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;

    private static final String VIEW_KEY_PREFIX = "product:views:"; //조회수
    private static final String VIEW_USERS_PREFIX = "product:viewed_users:"; //유저별 조회 이력

    /**
     * 상품 조회 시 조회수 증가 (유저당 한 번만)
     */
    @Transactional
    public void incrementViewCount(Long productId, Long userId) {
        String viewedUsersKey = VIEW_USERS_PREFIX + productId;

        // Redis Set에 유저 ID 추가
        Long addedCount = redisTemplate.opsForSet().add(viewedUsersKey, userId);
        if (addedCount != null && addedCount > 0) {
            // 유저가 처음 조회한 경우 조회수 증가
            String redisKey = VIEW_KEY_PREFIX + productId;
            redisTemplate.opsForValue().increment(redisKey);

            // 유저별 조회 기록에 만료 시간 설정 (7일 후 만료)
            redisTemplate.expire(viewedUsersKey, Duration.ofDays(7));
        }
    }

    /**
     * 상품 조회수 가져오기
     */
    @Transactional(readOnly = true)
    public int getViewCount(Long productId) {
        String redisKey = VIEW_KEY_PREFIX + productId;

        // Redis에서 조회수 가져오기
        Integer viewCount = (Integer) redisTemplate.opsForValue().get(redisKey);

        // Redis에 데이터가 없으면 DB에서 조회
        if (viewCount == null) {
            ProductEntity product = productRepository.findByProductId(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
            viewCount = product.getViewCount();
            redisTemplate.opsForValue().set(redisKey, viewCount); // Redis에 캐싱
        }
        return viewCount;
    }

    /**
     * Redis 데이터를 주기적으로 DB에 동기화
     */
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void syncViewsToDatabase() {
        Set<String> keys = redisTemplate.keys(VIEW_KEY_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                Integer viewCount = (Integer) redisTemplate.opsForValue().get(key);
                if (viewCount != null) {
                    Long productId = extractProductIdFromKey(key);
                    productRepository.findByProductId(productId).ifPresent(product -> {
                        product.setViewCount(viewCount);
                        productRepository.save(product);
                    });
                }
            }
        }
    }

    private Long extractProductIdFromKey(String key) {
        return Long.parseLong(key.replace(VIEW_KEY_PREFIX, ""));
    }
}
