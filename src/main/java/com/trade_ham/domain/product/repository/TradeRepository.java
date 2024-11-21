package com.trade_ham.domain.product.repository;

import com.trade_ham.domain.product.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeRepository extends JpaRepository<TradeEntity, Long> {
    Optional<TradeEntity> findByProduct_Id(Long productId);
}
