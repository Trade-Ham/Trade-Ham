package com.trade_ham.domain.product.controller;

import com.trade_ham.domain.product.dto.ProductResponseDTO;
import com.trade_ham.domain.product.service.ViewLikeProductService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ViewLikeProductController {

    private final ViewLikeProductService viewLikeProductService;

    @PatchMapping("/like/{productId}")
    public ApiResponse<String> clickLikeButton(@AuthenticationPrincipal Long userId, @PathVariable Long productId) {
        viewLikeProductService.incrementLike(userId, productId);

        return ApiResponse.success("좋아요 완료");
    }

    @PatchMapping("/like/cancel/{productId}")
    public ApiResponse<String> cancelLikeButton(@AuthenticationPrincipal Long userId, @PathVariable Long productId) {
        viewLikeProductService.decrementLike(userId, productId);

        return ApiResponse.success("좋아요 취소 완료");
    }

    @GetMapping("/likes")
    public ApiResponse<List<ProductResponseDTO>> getUserLikes(@AuthenticationPrincipal Long userId) {
        List<ProductResponseDTO> productResponseDTOS = viewLikeProductService.findUserLikeProducts(userId);

        return ApiResponse.success(productResponseDTOS);
    }
}
