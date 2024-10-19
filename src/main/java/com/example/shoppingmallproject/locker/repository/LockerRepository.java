package com.example.shoppingmallproject.locker.repository;

import com.example.shoppingmallproject.locker.domain.Locker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerRepository extends JpaRepository<Locker, Long> {
}