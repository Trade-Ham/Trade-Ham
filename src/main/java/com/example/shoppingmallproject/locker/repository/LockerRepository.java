package com.example.shoppingmallproject.locker.repository;

import com.example.shoppingmallproject.locker.domain.Locker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {

    // 현재 사용되지 않은 빈 사물함을 lockerNumber 순서로 정렬하여 가장 낮은 번호의 사물함을 가져오는 쿼리
    Optional<Locker> findFirstByLockerStatusFalseOrderByLockerNumberAsc();

    // 모든 사용 가능한 빈 사물함을 반환
    List<Locker> findAllByLockerStatusFalse();
}
