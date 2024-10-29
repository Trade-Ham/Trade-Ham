package com.example.shoppingmallproject.controller;

import com.example.shoppingmallproject.dto.LockerStatusDTO;
import com.example.shoppingmallproject.entity.Locker;
import com.example.shoppingmallproject.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LockerController {

    private final LockerService lockerService;

    @GetMapping("/admin/lockers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LockerStatusDTO>> getLockersStatus() {
        List<Locker> lockers = lockerService.getAllLockers();
        List<LockerStatusDTO> lockerStatuses = lockers.stream()
                .map(locker -> new LockerStatusDTO(locker.getId(), locker.getLockerStatus()))
                .toList();
        return ResponseEntity.ok(lockerStatuses);
    }
}
