package com.trade_ham.domain.product.controller;

import com.trade_ham.domain.auth.dto.CustomOAuth2User;
import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.entity.ProductStatus;
import com.trade_ham.domain.product.service.PurchaseProductService;
import com.trade_ham.global.common.exception.AccessDeniedException;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class PurchaseProductController {
    private final PurchaseProductService productService;

    @GetMapping("/purchase-page/{productId}")
    public ApiResponse<String> accessPurchasePage(@PathVariable Long productId) {
        productService.purchaseProduct(productId);
        return ApiResponse.success("구매 페이지에 접근 가능합니다.");
    }

    @PostMapping("/purchase/{product_id}")
    public ApiResponse<String> completePurchase(@PathVariable Long product_id, @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        Long buyerId = oAuth2User.getId();
        productService.completePurchase(product_id, buyerId);
        return ApiResponse.success("구매를 완료하였습니다.");
    }

    @PostMapping("/locker-in/{product_id}")
    public ApiResponse<String> storeInLocker(@PathVariable Long product_id) {
        productService.storeInLocker(product_id);
        return ApiResponse.success("물건을 사물함에 넣었습니다.");
    }

    @PostMapping("/take-out/{product_id}")
    public ApiResponse<String> markAsReceived(@PathVariable Long product_id) {
        productService.markAsReceived(product_id);
        return ApiResponse.success("물건을 수령하였습니다.");
    }


}