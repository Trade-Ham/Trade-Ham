package com.example.shoppingmallproject.service;

import com.example.shoppingmallproject.dto.ProductCreatedDTO;
import com.example.shoppingmallproject.dto.ProductUpdateDTO;
import com.example.shoppingmallproject.entity.Product;
import com.example.shoppingmallproject.entity.Status;
import com.example.shoppingmallproject.entity.User;
import com.example.shoppingmallproject.jwt.JWTUtil;
import com.example.shoppingmallproject.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public Product createProduct(ProductCreatedDTO productCreatedDTO) {
        User seller = userService.findUserById(productCreatedDTO.getSellerId());

        Product product = new Product();
        product.setSeller(seller);
        product.setName(productCreatedDTO.getName());
        product.setDescription(productCreatedDTO.getDescription());
        product.setStatus(productCreatedDTO.getStatus());
        product.setPrice(productCreatedDTO.getPrice());

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductUpdateDTO productUpdateDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        product.setName(productUpdateDTO.getName());
        product.setDescription(productUpdateDTO.getDescription());
        product.setPrice(productUpdateDTO.getPrice());

        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found with id: " + productId);
        }

        productRepository.deleteById(productId);
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
    }


    public List<Product> getMyProducts(HttpServletRequest request) {
        String email = null;
        if (request.getHeader("access") != null) {
            String token = request.getHeader("access");
            email = jwtUtil.getEmailInService(token);
        } else {
            throw new IllegalArgumentException("JWT 토큰이 유효하지 않습니다.");
        }

        List<Product> products = productRepository.findBySellerEmail(email);
        return products;
    }

    public List<Product> getSellProducts() {
        return productRepository.findByStatus(Status.SELL);
    }
}
