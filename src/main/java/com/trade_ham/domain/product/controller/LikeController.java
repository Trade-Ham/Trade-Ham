package com.trade_ham.domain.product.controller;

import com.trade_ham.domain.auth.dto.CustomOAuth2User;
import com.trade_ham.domain.product.entity.LikeEntity;
import com.trade_ham.domain.product.service.LikeService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {

    private final LikeService likeService;

    // 상품 좋아요 클릭
    @PostMapping("/likes/{productId}")
    public ApiResponse<String> toggleLike(@PathVariable Long productId, @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        boolean isLiked = likeService.toggleLike(productId, oAuth2User.getId());

        if (isLiked) {
            return ApiResponse.success("좋아요 추가");
        } else {
            return ApiResponse.success("좋아요 취소");
        }
    }


    // 상품별 좋아요 cnt 조회
    @GetMapping("/products/{productId}/likes")
    public ApiResponse<Long> getLikeCount(@PathVariable Long productId) {
        Long likeCount = likeService.getLikeCount(productId);

        return ApiResponse.success(likeCount);
    }

    // 특정 유저가 좋아요한 상품 조회
    @GetMapping("/products/likes/{userId}")
    public ApiResponse<List<LikeEntity>> getLikedProductsByUser(@PathVariable Long userId) {
        List<LikeEntity> likeEntities = likeService.getLikedProductsByUser(userId);
        return ApiResponse.success(likeEntities);
    }
}
