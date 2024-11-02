package com.example.shoppingmallproject.login.repository;

import com.example.shoppingmallproject.login.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u JOIN FETCH u.products WHERE u.id = :userId")
    Optional<User> findUserWithProductsById(@Param("userId") Long userId);
}
