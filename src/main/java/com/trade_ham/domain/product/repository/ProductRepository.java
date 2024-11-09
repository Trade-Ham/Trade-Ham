package com.trade_ham.domain.product.repository;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.entity.ProductStatus;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByProductId(Long productId);
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
    List<ProductEntity> findBySeller(UserEntity seller);
    List<ProductEntity> findByBuyer(UserEntity buyer);
    List<ProductEntity> findByStatusOrderByCreatedAtDesc(ProductStatus status);
    @Query("SELECT p FROM ProductEntity p WHERE p.status = ProductStatus.SELL AND p.name LIKE %:keyword%")
    List<ProductEntity> findByKeywordContainingAndStatusIsSell(@Param("keyword") String keyword);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.productId = :productId")
    Optional<ProductEntity> findByIdWithPessimisticLock(@Param("productId") Long productId);
}

