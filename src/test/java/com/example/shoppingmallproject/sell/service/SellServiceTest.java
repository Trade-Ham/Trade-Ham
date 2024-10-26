package com.example.shoppingmallproject.sell.service;

import com.example.shoppingmallproject.login.domain.ProviderType;
import com.example.shoppingmallproject.login.domain.RoleType;
import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.repository.UserRepository;
import com.example.shoppingmallproject.login.security.JwtTokenProvider;
import com.example.shoppingmallproject.sell.domain.Products;
import com.example.shoppingmallproject.sell.domain.StatusType;
import com.example.shoppingmallproject.sell.dto.ProductRequest;
import com.example.shoppingmallproject.sell.repository.SellRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class SellServiceTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private SellService sellService;
    @Autowired
    private SellRepository sellRepository;
    @Autowired
    private UserRepository userRepository;

    private ProductRequest productRequest;
    private String token;
    private User seller;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {

        seller = new User();
        seller.setNickname("seller");
        seller.setEmail("seller" + System.currentTimeMillis() + "@example.com"); // 고유한 이메일 생성
        seller.setRole(RoleType.USER);
        seller.setProvider(ProviderType.KAKAO);
        seller.setAccount("seller_account");
        seller.setRealname("Roki");
        seller.setProfileImage("default_image.png");

        seller = userRepository.save(seller);

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));
        token = jwtTokenProvider.createAccessToken(seller.getId(), authorities);
    }

    @Transactional
    @Test
    void 물건_올리기() {
        // given
        Long sellerId = seller.getId();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        productRequest = new ProductRequest();
        productRequest.setProductName("피그마 디자인");
        productRequest.setDescription("책입니다.");
        productRequest.setPrice(10000L);

        // when
        Long productId = sellService.createProduct(productRequest, request);

        // then
        assertThat(productId).isNotNull();

        Optional<Products> savedProductOptional = sellRepository.findById(productId);
        assertThat(savedProductOptional).isPresent();

        Products savedProduct = savedProductOptional.get();
        assertThat(savedProduct.getProductName()).isEqualTo(productRequest.getProductName());
        assertThat(savedProduct.getProductDescription()).isEqualTo(productRequest.getDescription());
        assertThat(savedProduct.getPrice()).isEqualTo(productRequest.getPrice());
        assertThat(savedProduct.getSeller().getId()).isEqualTo(sellerId);
    }

    @Transactional
    @Test
    void 물건_수정하기() {
        // given
        물건_올리기();
        Optional<Products> productsOptional = sellRepository.findAll().stream().findFirst();
        assertThat(productsOptional).isPresent();
        Long productId = productsOptional.get().getId();

        ProductRequest updatedRequest = new ProductRequest();
        updatedRequest.setProductName("피그마 디자인");
        updatedRequest.setDescription("도서입니다.");
        updatedRequest.setPrice(12000L);

        // when
        Long updatedProductId = sellService.updateProduct(productId, updatedRequest);

        Optional<Products> updatedProductOptional = sellRepository.findById(updatedProductId);
        assertThat(updatedProductOptional).isPresent();

        Products updatedProduct = updatedProductOptional.get();
        assertThat(updatedProduct.getProductName()).isEqualTo(updatedRequest.getProductName());
        assertThat(updatedProduct.getProductDescription()).isEqualTo(updatedRequest.getDescription());
        assertThat(updatedProduct.getPrice()).isEqualTo(updatedRequest.getPrice());
    }

    @Transactional
    @Test
    void 물건_삭제하기() {
        물건_올리기();
        Optional<Products> productOptional = sellRepository.findAll().stream().findFirst();
        assertThat(productOptional).isPresent();
        Long productId = productOptional.get().getId();

        sellService.deleteProduct(productId);

        Optional<Products> deletedProductOptional = sellRepository.findById(productId);
        assertThat(deletedProductOptional).isNotPresent();
    }

    @Transactional
    @Test
    void 판매_중인_물건_조회하기() {
        물건_올리기();
        List<Products> products = sellService.getAllProducts();
        assertThat(products).isNotEmpty();
        assertThat(products.get(0).getStatus()).isEqualTo(StatusType.SELL);
    }

    @Test
    void 내가_올린_물건_조회하기() {
        물건_올리기();

        List<Products> myProducts = sellService.getMyProducts(request);
        assertThat(myProducts).isNotEmpty();
        assertThat(myProducts.get(0).getSeller().getId()).isEqualTo(seller.getId());
    }

    @Transactional
    @Test
    void 물건_검색하기() {
        물건_올리기();

        String keyword = "피그마";
        List<Products> searchResults = sellService.searchProducts(keyword);
        assertThat(searchResults).isNotEmpty();
        assertThat(searchResults.get(0).getProductName()).contains(keyword);
    }
}