package com.example.shoppingmallproject.initializer;

import com.example.shoppingmallproject.entity.Locker;
import com.example.shoppingmallproject.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final LockerRepository lockerRepository;

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();

        for (int i = 101; i <= 103; i++) {
            Locker locker = new Locker();
            locker.setLockerNumber(String.valueOf(i));
            locker.setLockerPassword(generateRandomPassword(random));
            locker.setLockerStatus(false); // 또는 원하는 초기값 설정
            lockerRepository.save(locker);
        }
    }

    private String generateRandomPassword(Random random) {
        StringBuilder password = new StringBuilder();
        for (int j = 0; j < 4; j++) {
            password.append(random.nextInt(10));
        }

        return password.toString();
    }
}
