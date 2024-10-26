package com.example.shoppingmallproject.sell.repository;

import com.example.shoppingmallproject.sell.domain.Products;
import com.example.shoppingmallproject.sell.domain.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellRepository extends JpaRepository<Products, Long> {
    List<Products> findByStatus(StatusType status);
}
