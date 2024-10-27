package com.example.shoppingmallproject.common.config;

import com.example.shoppingmallproject.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private final UserService userService;

    public InitialDataLoader(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        userService.createInitialAdminUser();
    }
}