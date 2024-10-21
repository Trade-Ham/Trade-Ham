package com.example.shoppingmallproject.service;

import com.example.shoppingmallproject.entity.Locker;
import com.example.shoppingmallproject.repository.LockerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LockerService {

    private final LockerRepository lockerRepository;

    public LockerService(LockerRepository lockerRepository) {
        this.lockerRepository = lockerRepository;
    }

    public List<Locker> getAllLockers() {
        return lockerRepository.findAll();
    }
}
