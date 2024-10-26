package com.example.shoppingmallproject.sell.service;

import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.repository.UserRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellService {

    private final SellRepository sellRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Long createProduct(ProductRequest productRequest, HttpServletRequest request) {

        Long sellerId = jwtTokenProvider.checkTokenValidity(request);

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 생성자로 가능하지 않나 setter 쓰는 것보다
        Products product = new Products();
        product.setSellerId(seller);
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

    public List<Products> getAllProducts() {
        return sellRepository.findByStatus(StatusType.SELL);
    }

    public List<Products> getMyProducts(HttpServletRequest request) {

        Long userId = jwtTokenProvider.checkTokenValidity(request);
        User userInfo = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return userInfo.getProducts();
    }

    public List<Products> searchProducts(String keyword) {
        return sellRepository.searchByKeywordAndStatus(keyword, StatusType.SELL);
    }
}
