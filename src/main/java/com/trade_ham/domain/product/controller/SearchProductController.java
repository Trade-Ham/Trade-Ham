package com.trade_ham.domain.product.controller;


import com.trade_ham.domain.product.dto.ProductResponseDTO;
import com.trade_ham.domain.product.service.SearchProductService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class SearchProductController {
    private final SearchProductService searchProductService;

    // 물품 검색
    @GetMapping("/search")
    public ApiResponse<List<ProductResponseDTO>> searchProducts(@RequestParam String keyword) {
        List<ProductResponseDTO> products = searchProductService.searchProducts(keyword);

        return ApiResponse.success(products);
    }
}
