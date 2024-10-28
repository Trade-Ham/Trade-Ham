package com.example.shoppingmallproject.product.service;

import com.example.shoppingmallproject.common.exception.ResourceNotFoundException;
import com.example.shoppingmallproject.product.domain.Product;
import com.example.shoppingmallproject.product.dto.ProductDTO;
import com.example.shoppingmallproject.product.dto.ProductResponseDTO;
import com.example.shoppingmallproject.product.repository.ProductRepository;
import com.example.shoppingmallproject.user.domain.User;
import com.example.shoppingmallproject.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class SellProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SellProductService sellProductService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 상품을 정상적으로 생성할 때의 동작 테스트
    @Test
    void testCreateProduct_Success() {
        Long sellerId = 1L;
        ProductDTO productDTO = new ProductDTO("실무 피그마", "피그마 책입니다.", 10000L);
        User seller = new User();

        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));
        Product product = new Product().setSeller(seller);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO response = sellProductService.createProduct(productDTO, sellerId);

        assertEquals(product.getName(), response.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // 수정할 상품이 존재하지 않는 경우, ResourceNotFoundException 예외를 발생시키는지 테스트
    @Test
    void testUpdateProduct_ProductNotFound() {
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO("실무 피그마", "피그마 책입니다.", 15000L);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sellProductService.updateProduct(productId, productDTO));
    }

    // 상품 삭제가 정상적으로 이루어지는지 테스트
    @Test
    void testDeleteProduct_Success() {
        Long productId = 1L;
        Product product = new Product();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        sellProductService.deleteProduct(productId);

        verify(productRepository, times(1)).delete(product);
    }

    // 판매자가 존재하지 않는 경우, ResourceNotFoundException 예외를 발생시키는지 테스트
    @Test
    void testFindProductsBySeller_SellerNotFound() {
        Long sellerId = 1L;

        when(userRepository.findById(sellerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sellProductService.findProductsBySeller(sellerId));
    }
}