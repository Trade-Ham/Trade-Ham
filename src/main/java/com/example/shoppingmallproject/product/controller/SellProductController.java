package com.example.shoppingmallproject.product.controller;

import com.example.shoppingmallproject.common.response.ApiResponse;
import com.example.shoppingmallproject.product.dto.ProductDTO;
import com.example.shoppingmallproject.product.dto.ProductResponseDTO;
import com.example.shoppingmallproject.product.service.SellProductService;
import com.example.shoppingmallproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SellProductController {
    private final SellProductService sellProductService;
    private final UserService userService;

    // 물품 올리기
    @PostMapping("/product/sell")
    public ApiResponse<ProductResponseDTO> createProduct(@RequestBody ProductDTO productDTO) {
        Long sellerId = userService.getCurrentUserId();
        ProductResponseDTO product = sellProductService.createProduct(productDTO, sellerId);

        return ApiResponse.success(product);
    }

    // 물품 수정
    @PutMapping("/product/{productId}")
    public ApiResponse<ProductResponseDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        ProductResponseDTO updatedProduct = sellProductService.updateProduct(productId, productDTO);

        return ApiResponse.success(updatedProduct);
    }

    // 물품 삭제
    @DeleteMapping("/product/{productId}")
    public ApiResponse<String> deleteProduct(@PathVariable Long productId) {
        sellProductService.deleteProduct(productId);

        return ApiResponse.success("삭제 완료");
    }

    // 물품 검색
    @GetMapping("/product/search")
    public ApiResponse<List<ProductResponseDTO>> searchProducts(@RequestParam String keyword) {
        List<ProductResponseDTO> products = sellProductService.searchProducts(keyword);

        return ApiResponse.success(products);
    }

    // 판매자의 판매 내역 조회
    @GetMapping("/products/sell")
    public ApiResponse<List<ProductResponseDTO>> findProductsBySeller() {
        Long sellerId = userService.getCurrentUserId();
        List<ProductResponseDTO> products = sellProductService.findProductsBySeller(sellerId);

        return ApiResponse.success(products);
    }

    // 상태가 SELL인 전체 판매 물품 최신순으로 조회
    @GetMapping("/products")
    public ApiResponse<List<ProductResponseDTO>> findAllSellProducts() {
        List<ProductResponseDTO> products = sellProductService.findAllSellProducts();

        return ApiResponse.success(products);
    }
}