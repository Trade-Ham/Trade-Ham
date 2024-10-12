package com.example.shoppingmallproject.locker.repository;

import com.example.shoppingmallproject.locker.domain.Locker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {
    Optional<Locker> findByLockerNumberAndLockerStatusFalse(int lockerNumber);
    List<Locker> findAllByLockerStatusFalse();  // 모든 비어있는 사물함 조회
}
