package com.example.shoppingmallproject.locker.controller;

import com.example.shoppingmallproject.locker.domain.Locker;
import com.example.shoppingmallproject.locker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lockers")
public class LockerController {

    private final LockerRepository lockerRepository;


    @GetMapping
    public List<Locker> getAllLockers() {
        return lockerRepository.findAll();
    }
}