package com.trade_ham.domain.product.service;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.auth.repository.UserRepository;
import com.trade_ham.domain.product.entity.LikeEntity;
import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.entity.ProductStatus;
import com.trade_ham.domain.product.dto.ProductDTO;
import com.trade_ham.domain.product.dto.ProductResponseDTO;
import com.trade_ham.domain.product.repository.LikeRepository;
import com.trade_ham.domain.product.repository.ProductRepository;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    // 물품 올리기
    public ProductResponseDTO createProduct(ProductDTO productDTO, Long sellerId) {
        UserEntity seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));


        ProductEntity productEntity = ProductEntity.builder()
                .seller(seller)
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .status(ProductStatus.SELL)
                .price(productDTO.getPrice())
                .build();

        seller.addSellingProduct(productEntity);

        ProductEntity savedProductEntity = productRepository.save(productEntity);

        return new ProductResponseDTO(savedProductEntity);
    }

    // 물품 수정
    public ProductResponseDTO updateProduct(Long productId, ProductDTO productDTO) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        productEntity.updateProduct(productDTO.getName(), productDTO.getDescription(), productDTO.getPrice());

        ProductEntity updatedProductEntity = productRepository.save(productEntity);

        return new ProductResponseDTO(updatedProductEntity);
    }

    // 물품 삭제
    public void deleteProduct(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        // 판매자 판매 내역에서 해당 상품 제거
        UserEntity seller = productEntity.getSeller();
        if (seller != null) {
            seller.deleteSellingProduct(productEntity);
        }

        productRepository.delete(productEntity);
    }

    // 물품 검색 (이름을 기반으로 검색)
    // N+1 문제 발생 예상 지역
    public List<ProductResponseDTO> searchProducts(String keyword) {
        List<ProductEntity> productEntities = productRepository.findByNameContainingIgnoreCase(keyword);

        return productEntities.stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
    }



    // 상태가 SELL인 전체 판매 물품 최신순 조회
    // N+1 문제 발생 예상 지역
    public List<ProductResponseDTO> findAllSellProducts() {
        List<ProductEntity> productEntities = productRepository.findByStatusOrderByCreatedAtDesc(ProductStatus.SELL);

        return productEntities.stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 상태가 SELL인 전체 판매 물품 최신순 조회
    @Transactional
    public ProductResponseDTO findSellProduct(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        productEntity.setViews(productEntity.getViews() + 1);
        productRepository.save(productEntity); // 변경 사항 저장

        return new ProductResponseDTO(productEntity);
    }

    public void likeProduct(Long userId, Long productId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        // 좋아요 엔티티가 이미 존재하는지 확인
        Optional<LikeEntity> existingLike = likeRepository.findByUserAndProduct(user, product);

        if (existingLike.isPresent()) {
            // 좋아요가 이미 존재하면 삭제
            likeRepository.delete(existingLike.get());
        } else {
            // 좋아요가 존재하지 않으면 생성
            LikeEntity like = new LikeEntity();
            like.setUser(user);
            like.setProduct(product);
            likeRepository.save(like);
        }
    }

}