package com.example.shoppingmallproject.sell.controller;

import com.example.shoppingmallproject.sell.dto.ProductRequest;
import com.example.shoppingmallproject.sell.service.SellService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SellController {

    private final SellService sellService;

    @PostMapping("/product/sell")
    public ResponseEntity<Long> sellProduct(@RequestBody ProductRequest productRequest, HttpServletRequest request) {
        Long product_id = sellService.createProduct(productRequest, request);
        return ResponseEntity.ok(product_id);
    }

    @PatchMapping("/product/{product_id}")
    public ResponseEntity<Long> updateProduct(@PathVariable Long product_id, @RequestBody ProductRequest productRequest) {
        sellService.updateProduct(product_id, productRequest);
        return ResponseEntity.ok(product_id);
    }
}