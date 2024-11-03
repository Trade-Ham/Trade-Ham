package com.trade_ham.domain.product.service;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.auth.repository.UserRepository;
import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.entity.ProductStatus;
import com.trade_ham.domain.product.dto.ProductDTO;
import com.trade_ham.domain.product.dto.ProductResponseDTO;
import com.trade_ham.domain.product.repository.ProductRepository;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

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
}