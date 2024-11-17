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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final LikeRepository likeRepository;


    /**
     * 좋아요 추가
     */
    @Transactional
    public void likeProduct(Long productId, Long userId) {
        // 유저 및 상품 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        ProductEntity product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        // 이미 좋아요를 눌렀는지 확인
        Optional<LikeEntity> existingLike = likeRepository.findByUserAndProduct(user, product);
        if (existingLike.isPresent()) {
            throw new InvalidProductStateException(ErrorCode.DUPLICATE_LIKE);
        }

        // 좋아요 저장
        LikeEntity like = LikeEntity.of(user, product);
        likeRepository.save(like);

        // 상품의 좋아요 수 감소
        product.incrementLikeCount();
        productRepository.save(product);
    }

    /**
     * 좋아요 취소
     */
    @Transactional
    public void unlikeProduct(Long productId, Long userId) {
        // 유저 및 상품 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        ProductEntity product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        // 좋아요 삭제
        LikeEntity like = likeRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.Like_NOT_FOUND));
        likeRepository.delete(like);

        // 상품의 좋아요 수 감소
        product.decrementLikeCount();
        productRepository.save(product);
    }

    /**
     * 상품의 좋아요 개수 조회
     */
    @Transactional(readOnly = true)
    public int getLikeCount(Long productId) {
        ProductEntity product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        return product.getLikeCount();
    }
}
