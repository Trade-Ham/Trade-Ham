package com.trade_ham.domain.product.service;

import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.repository.ProductRepository;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.exception.ResourceNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchProductService {

    private final ProductRepository productRepository;

    public List<ProductEntity> searchSellProduct(String keyword) {
        return productRepository.findByKeywordContainingAndStatusIsSell(keyword);
    }


    // 상품 클릭 시 상세정보
    public ProductEntity getProductDetail(Long productId, HttpServletRequest request, HttpServletResponse response) {
        // 상품 존재 여부 확인
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        // 세션 쿠키에서 상품 조회 여부 확인
        if (!hasViewedProduct(request, productId)) {
            productRepository.increaseView(productId);
            addProductToViewedList(response, productId);
        }

        // 조회수 증가
        productRepository.increaseView(productId);

        // 증가된 조회수가 반영된 상품 정보 조회 (이미 존재 확인했으므 get() 사용 가능)
        return productRepository.findById(productId).get();
    }

    // 세션 쿠키에서 해당 상품을 조회했는지 확인
    private boolean hasViewedProduct(HttpServletRequest request, Long productId) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("viewedProducts".equals(cookie.getName()) && cookie.getValue().contains(String.valueOf(productId))) {
                    return true;    // 이미 조회한 상품
                }
            }
        }

        return false;   // 처음 조회한 상품
    }

    // 세션 쿠키에 조회한 상품 ID 추가
    private void addProductToViewedList(HttpServletResponse response, Long productId) {
        Cookie cookie = new Cookie("viewedProducts", String.valueOf(productId));
        cookie.setMaxAge(60 * 60 * 24 * 7); // 쿠키의 유효 기간을 1주일로 설정
        cookie.setPath("/"); // 전체 경로에서 쿠키 사용 가능
        response.addCookie(cookie);
    }
}
