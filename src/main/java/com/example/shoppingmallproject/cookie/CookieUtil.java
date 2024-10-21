package com.example.shoppingmallproject.cookie;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); https에서만 쿠키 진행 설정

        return cookie;
    }

    public Cookie initializeCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        return cookie;
    }
}
