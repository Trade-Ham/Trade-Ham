package com.example.shoppingmallproject.login.oauth2;

import com.example.shoppingmallproject.login.dto.TokenResponseDto;
import com.example.shoppingmallproject.login.service.AuthService;
import com.example.shoppingmallproject.login.service.RedisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        TokenResponseDto tokenResponse = authService.createJwtTokens(authentication);

        // Redis에 Refresh Token 저장 (7일 유효)
        redisService.saveRefreshToken(tokenResponse.getUserInfo().getEmail(), tokenResponse.getRefreshToken(), Duration.ofDays(7));

        // RefreshToken을 쿠키에 저장
        response.addCookie(createCookie("refresh", tokenResponse.getRefreshToken(), Duration.ofDays(7)));

        // AccessToken을 JSON 형태로 클라이언트에 응답 (react)
//        sendAccessToken(response, tokenResponse.getAccessToken());

//        response.setHeader("accessToken", tokenResponse.getAccessToken());
        // AccessToken을 HTML 페이지로 전달 (thymeleaf)
//        response.sendRedirect("/auth-success");
        response.sendRedirect("/auth-success?accessToken=" + URLEncoder.encode(tokenResponse.getAccessToken(), StandardCharsets.UTF_8));
        response.setStatus(HttpStatus.OK.value());
    }

    private void sendAccessToken(HttpServletResponse response, String accessToken) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"accessToken\": \"" + accessToken + "\"}");
    }

    private Cookie createCookie(String key, String value, Duration duration) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) duration.getSeconds());
        //cookie.setSecure(true); https
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
