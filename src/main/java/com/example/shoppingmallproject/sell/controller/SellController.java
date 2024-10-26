package com.example.shoppingmallproject.sell.controller;

import com.example.shoppingmallproject.sell.domain.Products;
import com.example.shoppingmallproject.sell.dto.ProductRequest;
import com.example.shoppingmallproject.sell.service.SellService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping("/product/{product_id}")
    public ResponseEntity<Long> deleteProduct(@PathVariable Long product_id) {
        sellService.deleteProduct(product_id);
        return ResponseEntity.ok(product_id);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Products>> getAllProducts() {
        List<Products> products = sellService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/sell")
    public ResponseEntity<List<Products>> getMyProducts(HttpServletRequest request) {
        List<Products> products = sellService.getMyProducts(request);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Products>> searchProducts(@RequestParam String keyword) {
        List<Products> products = sellService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }
}
