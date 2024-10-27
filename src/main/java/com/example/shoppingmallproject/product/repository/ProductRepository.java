package com.example.shoppingmallproject.product.repository;

import com.example.shoppingmallproject.product.domain.Product;
import com.example.shoppingmallproject.product.domain.ProductStatus;
import com.example.shoppingmallproject.user.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(Long productId);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findBySeller(User seller);
    List<Product> findByBuyer(User buyer);
    List<Product> findByStatusOrderByCreatedAtDesc(ProductStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId = :productId")
    Optional<Product> findByIdWithPessimisticLock(@Param("productId") Long productId);
}

