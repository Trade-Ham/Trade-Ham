package com.example.shoppingmallproject.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthPageController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    @GetMapping("/auth-success")
    public String authSuccess(@RequestParam String accessToken, Model model) {
        model.addAttribute("accessToken", accessToken);
        return "auth-success"; // auth-success.html 템플릿 반환
    }

}
