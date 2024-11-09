package com.trade_ham.domain.product.service;

import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchProductService {

    private final ProductRepository productRepository;

    public List<ProductEntity> searchSellProduct(String keyword) {
        return productRepository.findByKeywordContainingAndStatusIsSell(keyword);
    }
}
