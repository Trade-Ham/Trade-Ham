package com.example.shoppingmallproject.product.controller;

import com.example.shoppingmallproject.common.exception.AccessDeniedException;
import com.example.shoppingmallproject.common.exception.ErrorCode;
import com.example.shoppingmallproject.common.response.ApiResponse;
import com.example.shoppingmallproject.product.domain.Product;
import com.example.shoppingmallproject.product.domain.ProductStatus;
import com.example.shoppingmallproject.product.service.PurchaseProductService;
import com.example.shoppingmallproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PurchaseProductController {
    private final PurchaseProductService productService;
    private final UserService userService;

    @GetMapping("/product/purchase-page/{productId}")
    public ApiResponse<String> accessPurchasePage(@PathVariable Long productId) {
        Product product = productService.findProductById(productId);

        // 상태가 SELL이 아니라면 예외 발생
        if (!product.getStatus().equals(ProductStatus.SELL)) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED);
        }

        productService.purchaseProduct(productId);

        // 상태가 SELL이면 구매 페이지에 접근 가능
        return ApiResponse.success("구매 페이지에 접근 가능합니다.");
    }

    // 구매자의 구매 내역 조회
    @GetMapping("/products/purchase")
    public ApiResponse<List<Product>> findProductsByBuyer() {
        Long buyerId = userService.getCurrentUserId();
        List<Product> products = productService.findProductsByBuyer(buyerId);

        return ApiResponse.success(products);
    }
}