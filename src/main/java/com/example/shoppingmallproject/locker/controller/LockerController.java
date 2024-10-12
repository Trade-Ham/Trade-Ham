package com.example.shoppingmallproject.locker.controller;

import com.example.shoppingmallproject.locker.domain.Locker;
import com.example.shoppingmallproject.locker.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lockers")
@RequiredArgsConstructor
public class LockerController {

    private final LockerService lockerService;

    /**
     * 특정 번호의 사물함을 할당하는 API
     */
    @PostMapping("/{locker_number}")
    public ResponseEntity<Locker> assignLocker(@PathVariable("locker_number") int lockerNumber) {
        try {
            Locker assignedLocker = lockerService.assignLocker(lockerNumber);
            return ResponseEntity.ok(assignedLocker);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * 이용 가능한 모든 빈 사물함을 조회하는 API
     */
    @GetMapping("")
    public ResponseEntity<List<Locker>> retrieveAllAvailableLockers() {
        List<Locker> availableLockers = lockerService.retrieveAllAvailableLockers();
        if (availableLockers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(availableLockers);
    }
}
