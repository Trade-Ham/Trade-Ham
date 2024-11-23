package com.trade_ham.domain.product.service;

import com.trade_ham.domain.product.dto.ProductResponseDTO;
import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.repository.ProductRepository;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ViewLikeProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, String> redisSetTemplate; // Set 타입 Redis 관리


    public static final String USER_LIKED_PRODUCTS_KEY_PREFIX = "user:like:products:";


    /*
    좋아요 클릭 이벤트
    레디스 : key=userId, value=set(유저가 좋아요를 누른 게시물)
    사용자 좋아요 수를 올리고 레디스에 사용자가 좋아요 누른 게시물을 추가
     */
    @Transactional
    public void incrementLike(Long userId, Long productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        String userLikedProductsKey = USER_LIKED_PRODUCTS_KEY_PREFIX + userId;

        Long temp_size = redisSetTemplate.opsForSet().size(userLikedProductsKey);

        // 사용자가 좋아요한 게시물 목록에 추가
        redisSetTemplate.opsForSet().add(userLikedProductsKey, String.valueOf(productId));

        // 좋아요 이미 좋아요가 없을 때만 좋아요 증가 반영
        if (!Objects.equals(temp_size, redisSetTemplate.opsForSet().size(userLikedProductsKey))) {
            productEntity.increaseLikes();
        }

    }

    @Transactional
    public void decrementLike(Long userId, Long productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        String userLikedProductsKey = USER_LIKED_PRODUCTS_KEY_PREFIX + userId;

        Long temp_size = redisSetTemplate.opsForSet().size(userLikedProductsKey);

        // 사용자가 좋아요한 게시물 목록에서 제거
        redisSetTemplate.opsForSet().remove(userLikedProductsKey, String.valueOf(productId));

        // 사용자가 좋아요한 게시물 목록에 있었을 때만 제거
        if (!Objects.equals(temp_size, redisSetTemplate.opsForSet().size(userLikedProductsKey))) {
            productEntity.decreaseLikes();
        }
    }

    public List<ProductResponseDTO> findUserLikeProducts(Long userId) {
        String userLikedProductsKey = USER_LIKED_PRODUCTS_KEY_PREFIX + userId;

        Set<String> likedProductIds = redisSetTemplate.opsForSet().members(userLikedProductsKey);

        // Redis에 좋아요한 게시물이 없는 경우 빈 리스트 반환
        if (likedProductIds == null || likedProductIds.isEmpty()) {
            return List.of();
        }

        // 데이터베이스에서 해당 게시물 ID로 조회
        List<Long> productIds = likedProductIds.stream()
                .map(Long::valueOf)
                .toList();

        List<ProductEntity> productEntities = productRepository.findAllById(productIds);

        return productEntities.stream()
                .map(product -> {
                    ProductResponseDTO responseDTO = new ProductResponseDTO(product);
                    responseDTO.setIsLiked(true); // 사용자가 좋아요를 누른 게시물이므로 true로 설정
                    return responseDTO;
                })
                .toList();
    }
}
