package com.trade_ham.domain.product.repository;

import com.trade_ham.domain.product.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {}
