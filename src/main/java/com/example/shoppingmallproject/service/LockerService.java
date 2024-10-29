package com.example.shoppingmallproject.service;

import com.example.shoppingmallproject.entity.Locker;
import com.example.shoppingmallproject.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LockerService {

    private final LockerRepository lockerRepository;

    public List<Locker> getAllLockers() {
        return lockerRepository.findAll();
    }
}
