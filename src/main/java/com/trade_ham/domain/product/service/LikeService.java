package com.trade_ham.domain.product.service;

import com.trade_ham.domain.auth.repository.UserRepository;
import com.trade_ham.domain.product.entity.LikeEntity;
import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.repository.LikeRepository;
import com.trade_ham.domain.product.repository.ProductRepository;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LIKE_KEY_PREFIX = "like:";

    // 상품 좋아요 토글
    @Transactional
    public boolean toggleLike(Long userId, Long productId) {
        validateUserAndProductExistence(userId, productId);

        // Redis에 저장된 LikeEntity 조회
        String redisKey = LIKE_KEY_PREFIX + userId + ":" + productId;
        LikeEntity existingLike = (LikeEntity) redisTemplate.opsForValue().get(redisKey);

        if (existingLike != null) {
            // 좋아요가 있으면 Redis에서 삭제 (취소)
            redisTemplate.delete(redisKey);

            // Redis에서 좋아요 수 감소
            String likeCountKey = LIKE_KEY_PREFIX + productId;
            redisTemplate.opsForValue().decrement(likeCountKey);

            return false;
        } else {
            // Redis에 값이 없으면 DB에서 조회
            Optional<LikeEntity> dbLike = likeRepository.findByUserIdAndProductId(userId, productId);

            if (dbLike.isEmpty()) {
                // DB에도 없으면, 좋아요 추가
                LikeEntity likeEntity = LikeEntity.builder()
                        .user(userRepository.findById(userId).get())
                        .product(productRepository.findById(productId).get())
                        .build();

                // Redis에 좋아요 상태 저장 (TTL 2시간)
                redisTemplate.opsForValue().set(redisKey, likeEntity, 2, TimeUnit.HOURS);

                // Redis에서 좋아요 수 증가
                incrementLike(userId, productId);

                return true;
            } else {
                dbLike.ifPresent(likeRepository::delete);
                decrementLike(userId, productId);

                return false;
            }
        }
    }

    // 좋아요 증가 (DB 업데이트 하지 않음)
    @Transactional
    public void incrementLike(Long userId, Long productId) {
        // 유저 및 상품 검증
        validateUserAndProductExistence(userId, productId);

        String redisKey = LIKE_KEY_PREFIX + productId;

        // Redis에서 좋아요 수 조회
        String likeCount = (String) redisTemplate.opsForValue().get(redisKey);

        if (likeCount != null) {
            // Redis에 값이 있으면 그 값을 +1 시킴
            redisTemplate.opsForValue().increment(redisKey);
        } else {
            // DB에 값이 있으면 그 값을 +1 시킴
            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

            Long likeCountFromDb = product.getLikeCount();

            if (likeCountFromDb == 0) {
                // Redis에 값이 없으면 초기값을 설정
                redisTemplate.opsForValue().set(redisKey, "1");
            } else {
                // DB에 좋아요 수가 0이 아니면 Redis에 해당 값을 저장
                redisTemplate.opsForValue().set(redisKey, String.valueOf(likeCountFromDb));
            }

            // Redis에 좋아요 수 증가
            redisTemplate.opsForValue().increment(redisKey);
        }
    }

    // 좋아요 감소 (DB 업데이트 하지 않음)
    @Transactional
    public void decrementLike(Long userId, Long productId) {
        // 유저 및 상품 검증
        validateUserAndProductExistence(userId, productId);

        String redisKey = LIKE_KEY_PREFIX + productId;

        String likeCount = (String) redisTemplate.opsForValue().get(redisKey);

        redisTemplate.opsForValue().decrement(redisKey);
    }

    // 스케줄러로 Redis에 저장된 데이터를 DB에 합치고 Redis에서 삭제하는 작업
    @Scheduled(cron = "0 0 * * * *") // 10분 마다 실행
    @Transactional
    public void syncLikesFromRedisToDb() {
        // Redis에서 모든 like:로 시작하는 키 가져오기
        Set<String> redisKeys = redisTemplate.keys(LIKE_KEY_PREFIX + "*");

        if (redisKeys != null && !redisKeys.isEmpty()) {
            for (String redisKey : redisKeys) {
                Long productId = Long.valueOf(redisKey.replace(LIKE_KEY_PREFIX, ""));
                String redisValue = (String) redisTemplate.opsForValue().get(redisKey);

                if (redisValue != null) {
                    Long likeCountFromRedis = Long.valueOf(redisValue);

                    // DB에서 현재 좋아요 수 조회
                    Long likeCountFromDb = likeRepository.countByProductId(productId);

                    // DB에 Redis 값을 더하여 업데이트
                    Long totalLikes = likeCountFromRedis + likeCountFromDb;
                    likeRepository.updateLikeCount(productId, totalLikes);

                    // Redis 데이터 삭제
                    redisTemplate.delete(redisKey);
                }
            }
        }
    }

    // 상품 좋아요 수 조회
    public Long getLikeCount(Long productId) {
        String redisKey = LIKE_KEY_PREFIX + productId;

        // Redis에서 조회
        String likeCount = (String) redisTemplate.opsForValue().get(redisKey);

        if (likeCount != null) {
            // Redis에 값이 있으면 반환
            return Long.valueOf(likeCount);
        } else {
            // Redis에 값이 없으면 DB에서 조회하고 Redis에 캐싱
            Long countFromDb = likeRepository.countByProductId(productId);
            redisTemplate.opsForValue().set(redisKey, String.valueOf(countFromDb), 2, TimeUnit.HOURS);
            return countFromDb;
        }
    }

    // 유저가 좋아요한 상품 목록 조회
    public List<LikeEntity> getLikedProductsByUser(Long userId) {
        // 유저 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        return likeRepository.findByUserId(userId);
    }

    private void validateUserAndProductExistence(Long userId, Long productId) {
        // 유저 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 상품 존재 여부 확인
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
