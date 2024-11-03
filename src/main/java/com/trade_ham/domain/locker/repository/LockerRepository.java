package com.trade_ham.domain.locker.repository;

import com.trade_ham.domain.locker.entity.LockerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LockerRepository extends JpaRepository<LockerEntity, Long> {
    Optional<LockerEntity> findFirstByLockerStatusTrue();
}