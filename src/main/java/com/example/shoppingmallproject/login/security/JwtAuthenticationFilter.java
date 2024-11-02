package com.example.shoppingmallproject.login.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //of로 해볼까
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getParameter("accessToken");
        // 1. 요청 헤더에서 Authorization 헤더를 추출
//        String token = jwtTokenProvider.resolveToken(request);
        // 2. 토큰이 존재하고 유효할 경우, 사용자 정보를 설정
        if (token != null && jwtTokenProvider.validateToken(token)) {

            if (jwtTokenProvider.isTokenExpired(token)) {
                PrintWriter writer = response.getWriter();
                writer.print("access token expired");

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 3. 토큰에서 사용자 정보 추출
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // 4. SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 필터 체인에 요청을 넘겨 다음 필터로 처리 진행
        filterChain.doFilter(request, response);
    }
}
