package com.trade_ham.domain.product.service;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.auth.repository.UserRepository;
import com.trade_ham.domain.product.entity.LikeEntity;
import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.repository.LikeRepository;
import com.trade_ham.domain.product.repository.ProductRepository;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.exception.InvalidProductStateException;
import com.trade_ham.global.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final LikeRepository likeRepository;
    private final RedisTemplate<String, Integer> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "product:likes:";

    /**
     * 좋아요 추가
     */
    @Transactional
    public void likeProduct(Long productId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        Optional<LikeEntity> existingLike = likeRepository.findByUserAndProduct(user, product);
        if (existingLike.isPresent()) {
            throw new InvalidProductStateException(ErrorCode.DUPLICATE_LIKE);
        }

        LikeEntity like = LikeEntity.of(user, product);
        likeRepository.save(like);

        // 좋아요 수 Redis에 저장 (증가)
        incrementLikeCount(productId);
    }

    /**
     * 좋아요 취소
     */
    @Transactional
    public void unlikeProduct(Long productId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        LikeEntity like = likeRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.Like_NOT_FOUND));
        likeRepository.delete(like);

        // 좋아요 수 Redis에서 감소
        decrementLikeCount(productId);
    }

    /**
     * Redis에서 좋아요 수 증가
     */
    private void incrementLikeCount(Long productId) {
        String redisKey = REDIS_KEY_PREFIX + productId;
        redisTemplate.opsForValue().increment(redisKey);
    }

    /**
     * Redis에서 좋아요 수 감소
     */
    private void decrementLikeCount(Long productId) {
        String redisKey = REDIS_KEY_PREFIX + productId;
        redisTemplate.opsForValue().decrement(redisKey);
    }

    /**
     * 좋아요 수 조회
     */
    public int getLikeCount(Long productId) {
        String redisKey = REDIS_KEY_PREFIX + productId;
        Integer likeCount = redisTemplate.opsForValue().get(redisKey);

        // Redis에 데이터가 없으면 DB에서 조회
        if (likeCount == null) {
            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
            likeCount = product.getLikeCount();
            redisTemplate.opsForValue().set(redisKey, likeCount); // Redis에 캐싱
        }

        return likeCount;
    }
}
