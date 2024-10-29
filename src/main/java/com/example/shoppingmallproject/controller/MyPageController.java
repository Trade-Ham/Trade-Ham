package com.example.shoppingmallproject.controller;

import com.example.shoppingmallproject.entity.Product;
import com.example.shoppingmallproject.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/my")
public class MyPageController {

    private final ProductService productService;

    @GetMapping("/sell")
    public ResponseEntity<List<Product>> getMyProducts(HttpServletRequest request) {
        List<Product> myProducts = productService.getMyProducts(request);
        return ResponseEntity.ok(myProducts);
    }
}
