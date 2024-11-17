package com.trade_ham.domain.product.controller;

import com.trade_ham.domain.auth.dto.CustomOAuth2User;
import com.trade_ham.domain.product.service.ViewProductService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/{productId}/views")
public class ViewProductController {

    private final ViewProductService viewProductService;

    @PostMapping
    public ApiResponse<String> incrementViewCount(@PathVariable Long productId, @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        viewProductService.incrementViewCount(productId, oAuth2User.getId());
        return ApiResponse.success("조회수가 증가했습니다.");
    }

    @GetMapping
    public ApiResponse<Integer> getViewCount(@PathVariable Long productId) {
        int viewCount = viewProductService.getViewCount(productId);
        return ApiResponse.success(viewCount);
    }
}
