package com.example.shoppingmallproject.common.config;

import com.example.shoppingmallproject.locker.domain.Locker;
import com.example.shoppingmallproject.locker.repository.LockerRepository;
import com.example.shoppingmallproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final UserService userService;
    private final LockerRepository lockerRepository;

    @Override
    public void run(String... args) {
        userService.createInitialAdminUser();
        lockerRepository.save(new Locker("1", "1234", false));
        lockerRepository.save(new Locker("2", "1234", false));
        lockerRepository.save(new Locker("3", "1234", false));
    }
}