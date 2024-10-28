package com.trade_ham.global.config;

import com.trade_ham.domain.locker.domain.Locker;
import com.trade_ham.domain.locker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final LockerRepository lockerRepository;

    @Override
    public void run(String... args) {
        lockerRepository.save(new Locker("1", "1234", false));
        lockerRepository.save(new Locker("2", "1234", false));
        lockerRepository.save(new Locker("3", "1234", false));
    }
}