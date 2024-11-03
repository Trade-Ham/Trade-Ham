package com.trade_ham.domain.product.repository;

import com.trade_ham.domain.product.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<TradeEntity, Long> {}
