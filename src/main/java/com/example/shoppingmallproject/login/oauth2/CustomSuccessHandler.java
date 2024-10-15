package com.example.shoppingmallproject.login.oauth2;

import com.example.shoppingmallproject.login.dto.CustomOAuth2User;
import com.example.shoppingmallproject.login.dto.TokenResponseDto;
import com.example.shoppingmallproject.login.security.JwtTokenProvider;
import com.example.shoppingmallproject.login.service.AuthService;
import com.example.shoppingmallproject.login.service.RedisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        TokenResponseDto tokenResponse = authService.createJwtTokens();

        // Redis에 Refresh Token 저장 (7일 유효)
        redisService.saveRefreshToken(tokenResponse.getUserInfo().getEmail(), tokenResponse.getRefreshToken(), Duration.ofDays(7));
        response.addCookie(createCookie("Authorization", tokenResponse.getRefreshToken(), Duration.ofDays(7)));
        response.sendRedirect("/");
    }

    private Cookie createCookie(String key, String value, Duration duration) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) duration.getSeconds());
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
