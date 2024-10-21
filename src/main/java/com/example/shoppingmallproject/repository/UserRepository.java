package com.example.shoppingmallproject.repository;

import com.example.shoppingmallproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}