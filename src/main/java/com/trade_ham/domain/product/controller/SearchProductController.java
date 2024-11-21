package com.trade_ham.domain.product.controller;

import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.service.SearchProductService;
import com.trade_ham.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SearchProductController {

    private final SearchProductService searchProductService;

    @GetMapping("/search")
    public ApiResponse<List<ProductEntity>> searchSellProduct(@RequestParam String keyword) {
        List<ProductEntity> productEntities = searchProductService.searchSellProduct(keyword);

        return ApiResponse.success(productEntities);
    }

    // 상품 클릭
    @GetMapping("/products/{productId}")
    public ApiResponse<ProductEntity> getProductDetail(@PathVariable Long productId, HttpServletRequest request, HttpServletResponse response) {
        ProductEntity productEntity = searchProductService.getProductDetail(productId, request, response);

        return ApiResponse.success(productEntity);
    }
}
