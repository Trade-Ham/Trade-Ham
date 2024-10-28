package com.example.shoppingmallproject.user.controller;

import com.example.shoppingmallproject.user.domain.User;
import com.example.shoppingmallproject.user.dto.LoginReqDTO;
import com.example.shoppingmallproject.user.jwt.JwtTokenProvider;
import com.example.shoppingmallproject.user.repository.UserRepository;
import com.example.shoppingmallproject.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    @GetMapping("/auth/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/auth/admin-login")
    public String adminLoginPage() {
        return "admin-login";
    }

    @PostMapping("/auth/admin-login-process")
    public String adminLoginProcess(@RequestBody LoginReqDTO loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            String token = jwtTokenProvider.createAccessToken(user.getId());
            response.setHeader("Authorization", "Bearer " + token);
            return "redirect:/admin/home";
        } catch (AuthenticationException e) {
            return "redirect:/auth/admin-login?error";
        }
    }

    @GetMapping("/home")
    public String home(Model model) {
        String name = userService.getCurrentUserName();
        model.addAttribute("name", name);
        return "home";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "admin-home";
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
            String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken, userId);
            return ResponseEntity.ok().header("Authorization", "Bearer " + newAccessToken).build();
        } catch (Exception e) {
            log.error("token update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
