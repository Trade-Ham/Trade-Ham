package com.example.shoppingmallproject.controller;

import com.example.shoppingmallproject.dto.LockerStatusDTO;
import com.example.shoppingmallproject.entity.Locker;
import com.example.shoppingmallproject.service.LockerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LockerController {

    private final LockerService lockerService;

    public LockerController(LockerService lockerService) {
        this.lockerService = lockerService;
    }

    @GetMapping("/api/v1/admin/lockers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LockerStatusDTO>> getLockersStatus() {
        List<Locker> lockers = lockerService.getAllLockers();
        List<LockerStatusDTO> lockerStatuses = lockers.stream()
                .map(locker -> new LockerStatusDTO(locker.getId(), locker.getLockerStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lockerStatuses);
    }
}
