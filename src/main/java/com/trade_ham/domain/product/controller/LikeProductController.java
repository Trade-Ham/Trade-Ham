package com.trade_ham.domain.product.controller;

import com.trade_ham.domain.auth.dto.CustomOAuth2User;
import com.trade_ham.domain.product.service.LikeProductService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/like")
@RequiredArgsConstructor
public class LikeProductController {

    private final LikeProductService likeProductService;

    @PostMapping("")
    public ApiResponse<String> likeProduct(@PathVariable Long productId, @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        likeProductService.likeProduct(productId, oAuth2User.getId());
        return ApiResponse.success("좋아요가 추가되었습니다.");
    }

    @DeleteMapping("")
    public ApiResponse<String> unlikeProduct(@PathVariable Long productId, @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        likeProductService.unlikeProduct(productId, oAuth2User.getId());
        return ApiResponse.success("좋아요가 삭제되었습니다.");
    }

    @GetMapping("/count")
    public ApiResponse<Integer> getLikeCount(@PathVariable Long productId) {
        int likeCount = likeProductService.getLikeCount(productId);
        return ApiResponse.success(likeCount);
    }
}
