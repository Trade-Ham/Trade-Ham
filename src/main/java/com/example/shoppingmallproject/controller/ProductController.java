package com.example.shoppingmallproject.controller;

import com.example.shoppingmallproject.dto.ProductCreatedDTO;
import com.example.shoppingmallproject.dto.ProductUpdateDTO;
import com.example.shoppingmallproject.entity.Product;
import com.example.shoppingmallproject.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    //믈픔 등록
    @PostMapping("/product")
    public ResponseEntity<Product> createProduct(@RequestBody ProductCreatedDTO productCreatedDTO) {
        Product Product = productService.createProduct(productCreatedDTO);
        return ResponseEntity.ok(Product);
    }

    //물품 수정
    @PostMapping("/product/{product_id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long product_id,
            @RequestBody ProductUpdateDTO productUpdateDTO
    ) {
        Product Product = productService.updateProduct(product_id, productUpdateDTO);
        return ResponseEntity.ok(Product);
    }

    //물품 삭제
    @DeleteMapping("/product/{product_id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long product_id) {
        productService.deleteProduct(product_id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    //물품 검색
    @GetMapping("/product/{product_id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long product_id) {
        Product product = productService.getProduct(product_id);
        return ResponseEntity.ok(product);
    }

    //메인 페이지
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getSellProducts() {
        List<Product> products = productService.getSellProducts();
        return ResponseEntity.ok(products);
    }
}
