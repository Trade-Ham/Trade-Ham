package com.example.shoppingmallproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String redirectToKakaoLogin() {
        return "redirect:/oauth2/authorization/kakao";
    }

    @GetMapping("/welcome")
    @ResponseBody
    public String welcome() {
        return "로그인 성공";
    }
}
