package com.trade_ham.security.handler;

import com.trade_ham.domain.auth.dto.CustomOAuth2User;
import com.trade_ham.domain.auth.entity.RefreshEntity;
import com.trade_ham.domain.auth.repository.RefreshRepository;
import com.trade_ham.domain.auth.service.RedisRefreshService;
import com.trade_ham.security.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${spring.jwt.token.access-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private long refreshTokenExpirationTime;

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final RedisRefreshService redisRefreshService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        Long id = customUserDetails.getId();
        String email = customUserDetails.getEmail();


        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt("access", id, email, role, accessTokenExpirationTime);
        String refresh = jwtUtil.createJwt("refresh", id, email, role, refreshTokenExpirationTime);

        //Refresh 토큰 저장
        addRefreshEntity(id, refresh, refreshTokenExpirationTime);

        //응답 설정
        response.setHeader("access", access); // 응답헤더에 엑세스 토큰
        response.addCookie(createCookie("refresh", refresh)); // 응답쿠키에 리프레시 토큰
        //response.setStatus(HttpStatus.OK.value()); 추후 exception 코드로 변경
        response.sendRedirect("http://localhost:3000/");
    }

    private void addRefreshEntity(Long id, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        redisRefreshService.saveRefreshToken(id, refresh, expiredMs);
        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int)refreshTokenExpirationTime);
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
