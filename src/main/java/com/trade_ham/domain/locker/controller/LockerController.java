package com.trade_ham.domain.locker.controller;

import com.trade_ham.domain.locker.domain.LockerEntity;
import com.trade_ham.domain.locker.repository.LockerRepository;
import com.trade_ham.global.common.response.ApiResponse;
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
    public ApiResponse<List<LockerEntity>> getAllLockers() {
        return ApiResponse.success(lockerRepository.findAll());
    }
}