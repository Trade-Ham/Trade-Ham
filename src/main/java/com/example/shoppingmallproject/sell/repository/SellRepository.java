package com.example.shoppingmallproject.sell.repository;

import com.example.shoppingmallproject.sell.domain.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellRepository extends JpaRepository<Products, Long> {
}
