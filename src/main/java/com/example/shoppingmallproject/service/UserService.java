package com.example.shoppingmallproject.service;

import com.example.shoppingmallproject.entity.User;
import com.example.shoppingmallproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found wiht id: " + id));
    }
}
