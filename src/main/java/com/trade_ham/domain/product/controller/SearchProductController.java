package com.trade_ham.domain.product.controller;


import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.service.SearchProductService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SearchProductController {

    private final SearchProductService searchProductService;

    @GetMapping("/products/search")
    public ApiResponse<List<ProductEntity>> searchProducts(@RequestParam String keyword) {
        List<ProductEntity> products = searchProductService.searchProducts(keyword);
        return ApiResponse.success(products);
    }
}
