package com.trade_ham.domain.locker.repository;

import com.trade_ham.domain.locker.domain.Locker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LockerRepository extends JpaRepository<Locker, Long> {
    Optional<Locker> findFirstByLockerStatusTrue();
}