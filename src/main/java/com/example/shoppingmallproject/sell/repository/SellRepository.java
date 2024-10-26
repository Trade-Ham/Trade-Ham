package com.example.shoppingmallproject.sell.repository;

import com.example.shoppingmallproject.sell.domain.Products;
import com.example.shoppingmallproject.sell.domain.StatusType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellRepository extends JpaRepository<Products, Long> {
    List<Products> findByStatus(StatusType status);

    @Query("SELECT p FROM Products p WHERE p.status = :status AND (LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Products> searchByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") StatusType status);
}
