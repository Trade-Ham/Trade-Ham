package com.trade_ham.domain.mypage.service;


import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.auth.repository.UserRepository;
import com.trade_ham.domain.product.entity.LikeEntity;
import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.dto.ProductResponseDTO;
import com.trade_ham.domain.product.repository.LikeRepository;
import com.trade_ham.domain.product.repository.ProductRepository;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final LikeRepository likeRepository;

    // 구매자 구매 내역 관리
    public List<ProductEntity> findProductsByBuyer(Long buyerId) {
        UserEntity buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
        return productRepository.findByBuyer(buyer);
    }

    // 판매자 판매 내역 관리
    public List<ProductResponseDTO> findProductsBySeller(Long sellerId) {
        UserEntity seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
        List<ProductEntity> productEntities = productRepository.findBySeller(seller);

        return productEntities.stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 좋아요한 상품 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findLikedProducts(Long userId) {
        // 좋아요 정보를 통해 상품 목록 조회
        List<LikeEntity> likes = likeRepository.findByUserId(userId);
        return likes.stream()
                .map(like -> new ProductResponseDTO(like.getProduct()))
                .collect(Collectors.toList());
    }
}
