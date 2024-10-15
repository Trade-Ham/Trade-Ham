package com.example.shoppingmallproject.login.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //of로 해볼까
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
        String authorization = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if(Objects.equals(cookie.getName(), "Authorization")) {
                authorization = cookie.getValue();
            }
        }

        //Authorization 헤더 검증
        if (authorization == null) {
            filterChain.doFilter(request, response);
            //조건이 해당되면 메소드 종료 (필수)
            return ;
        }

        //  1. 요청 헤더에서 Authorization 헤더를 추출
        String token = authorization;

        // 2. 토큰이 존재하고 유효할 경우, 사용자 정보를 설정
        if (jwtTokenProvider.validateToken(token) && !jwtTokenProvider.isTokenExpired(token)) {
            // 3. 토큰에서 사용자 정보 추출해서 스프링 시큐리티 인증 토큰 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // 4. SecurityContext에 인증 정보 설정, 세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 필터 체인에 요청을 넘겨 다음 필터로 처리 진행
        filterChain.doFilter(request, response);
    }
}
