package com.example.shoppingmallproject.trade.repository;

import com.example.shoppingmallproject.sell.domain.Products;
import com.example.shoppingmallproject.trade.domain.Trades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trades, Long> {
    Optional<Trades> findByProduct(Products product);
}
