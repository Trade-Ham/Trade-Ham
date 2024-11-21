package com.trade_ham.domain.product.controller;

import com.trade_ham.domain.auth.dto.CustomOAuth2User;
import com.trade_ham.domain.product.service.PurchaseProductService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PurchaseProductController {
    private final PurchaseProductService productService;

    @GetMapping("/product/purchase-page/{productId}")
    public ApiResponse<String> accessPurchasePage(@PathVariable Long productId, @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        Long buyerId = oAuth2User.getId();

        productService.purchaseProduct(productId, buyerId);

        // 상태가 SELL이면 구매 페이지에 접근 가능
        return ApiResponse.success("구매 페이지에 접근 가능합니다.");
    }


}