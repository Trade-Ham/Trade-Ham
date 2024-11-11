package com.trade_ham.domain.product.service;

import com.trade_ham.domain.product.dto.ProductResponseDTO;
import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.entity.ProductStatus;
import com.trade_ham.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProducts(String keyword) {
        List<ProductEntity> products = productRepository.searchProducts(keyword);
        return products.stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
    }

}