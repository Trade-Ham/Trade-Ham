package com.trade_ham.domain.product.repository;


import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.product.entity.LikeEntity;
import com.trade_ham.domain.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByUserAndProduct(UserEntity user, ProductEntity product);
}
