package com.example.shoppingmallproject.user.service;

import com.example.shoppingmallproject.user.domain.User;
import com.example.shoppingmallproject.user.domain.UserRole;
import com.example.shoppingmallproject.user.domain.AuthProvider;
import com.example.shoppingmallproject.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(String email, String nickname, String password, UserRole role, AuthProvider provider, String account, String realname, String profileImage) {
        log.info("Creating new user with email: {}", email);
        User user = new User(email, nickname, passwordEncoder.encode(password), role, provider, null, null, profileImage);
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getEmail());
        return savedUser;
    }

    public String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return ((User) auth.getPrincipal()).getNickname();
        }
        return "Guest";
    }

    @Transactional
    public void createInitialAdminUser() {
        if (userRepository.count() == 0) {
            log.info("Creating initial admin user");
            createUser("admin@kakao.com", "Admin", "1234", UserRole.ADMIN, AuthProvider.KAKAO, null, "Admin", null);
        }
    }
}