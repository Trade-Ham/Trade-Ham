package com.example.shoppingmallproject.sell.service;

import com.example.shoppingmallproject.login.security.JwtTokenProvider;
import com.example.shoppingmallproject.sell.domain.Products;
import com.example.shoppingmallproject.sell.domain.StatusType;
import com.example.shoppingmallproject.sell.dto.ProductRequest;
import com.example.shoppingmallproject.sell.repository.SellRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SellService {

    private final SellRepository sellRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Long createProduct(ProductRequest productRequest, HttpServletRequest request) {

        Long sellerId = jwtTokenProvider.checkTokenValidity(request);

        // 생성자로 가능하지 않나 setter 쓰는 것보다
        Products product = new Products();
        product.setSellerId(sellerId);
        product.setProductName(productRequest.getProductName());
        product.setProductDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStatus(StatusType.SELL);

        Products savedProduct = sellRepository.save(product);

        return savedProduct.getId();
    }

    public Long updateProduct(Long productId, ProductRequest productRequest) {
        Products product = sellRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        product.setProductName(productRequest.getProductName());
        product.setProductDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());

        Products updatedProduct = sellRepository.save(product);

        return updatedProduct.getId();
    }

    public Long deleteProduct(Long productId) {
        Products product = sellRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        sellRepository.delete(product);
        return productId;
    }
}
