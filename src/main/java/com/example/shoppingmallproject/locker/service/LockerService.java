package com.example.shoppingmallproject.locker.service;

import com.example.shoppingmallproject.locker.domain.Locker;
import com.example.shoppingmallproject.locker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LockerService {

    private final LockerRepository lockerRepository;

    /**
     * 가장 낮은 번호의 빈 사물함을 할당하는 메소드
     */
    public Locker assignLocker() {
        Locker locker = lockerRepository.findFirstByLockerStatusFalseOrderByLockerNumberAsc()
                .orElseThrow(() -> new RuntimeException("No available lockers."));
        locker.setLockerStatus(false);
        return lockerRepository.save(locker);
    }

    public Locker unlockLocker(Long locker_id) {
        Locker locker = lockerRepository.findById(locker_id)
                .orElseThrow(() -> new RuntimeException("Locker Not Found."));
        locker.setLockerStatus(true);
        return lockerRepository.save(locker);
    }

    /**
     * 이용 가능한 빈 사물함 목록을 반환하는 메소드
     */
    public List<Locker> retrieveAllAvailableLockers() {
        return lockerRepository.findAllByLockerStatusFalse();
    }
}
