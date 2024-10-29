package com.example.shoppingmallproject.repository;

import com.example.shoppingmallproject.entity.Product;
import com.example.shoppingmallproject.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //이메일을 기준으로 판매자와 관련된 상품을 조회하는 메소드 (JWT)
    List<Product> findBySellerEmail(String email);

    //status가 SELL인 상품만 조회하는 메서드
    List<Product> findByStatus(Status status);
}