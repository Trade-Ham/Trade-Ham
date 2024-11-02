package com.example.shoppingmallproject.locker.service;

import com.example.shoppingmallproject.locker.domain.Locker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class LockerServiceTest {

    @Autowired
    private LockerService lockerService;

    @Test
    void 특정_사물함이_비어_있을_때_성공적으로_할당() {
        //When
        Locker assignedLocker = lockerService.assignLocker(1);

        //Then
        assertNotNull(assignedLocker);
        assertTrue(assignedLocker.getLockerStatus());
    }

    @Test
    void 사물함이_비어_있지_않으면_예외_발생() {
        assertThrows(RuntimeException.class, () -> lockerService.assignLocker(1));
    }

    @Test
    void 빈_사물함을_조회() {
        // When
        List<Locker> result = lockerService.retrieveAllAvailableLockers();
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
    }
}