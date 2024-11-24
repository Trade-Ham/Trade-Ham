package com.trade_ham.domain.mypage.controller;

import com.trade_ham.domain.auth.dto.CustomOAuth2User;
import com.trade_ham.domain.mypage.service.MyPageService;
import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.dto.ProductResponseDTO;
import com.trade_ham.domain.product.service.ViewLikeProductService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/my")
public class MyPageController {

    private final MyPageService myPageService;
    private final ViewLikeProductService viewLikeProductService;

    // 판매자의 판매 내역 조회
    @GetMapping("/sell")
    public ApiResponse<List<ProductResponseDTO>> findProductsBySeller(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        Long sellerId = oAuth2User.getId();
        List<ProductResponseDTO> products = myPageService.findProductsBySeller(sellerId);
        return ApiResponse.success(products);
    }

    // 구매자의 구매 내역 조회
    @GetMapping("/purchase")
    public ApiResponse<List<ProductEntity>> findProductsByBuyer(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        Long buyerId = oAuth2User.getId();
        List<ProductEntity> productEntities = myPageService.findProductsByBuyer(buyerId);

        return ApiResponse.success(productEntities);
    }

    @GetMapping("/likes")
    public ApiResponse<List<ProductResponseDTO>> getUserLikes(@AuthenticationPrincipal Long userId) {
        List<ProductResponseDTO> productResponseDTOS = viewLikeProductService.findUserLikeProducts(userId);

        return ApiResponse.success(productResponseDTOS);
    }
}
