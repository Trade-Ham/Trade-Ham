package com.example.shoppingmallproject.oauth2;

import com.example.shoppingmallproject.cookie.CookieUtil;
import com.example.shoppingmallproject.dto.CustomOAuth2User;
import com.example.shoppingmallproject.jwt.JWTUtil;
import com.example.shoppingmallproject.service.RefreshService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshService refreshService;
    private final CookieUtil cookieUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil, RefreshService refreshService, CookieUtil cookieUtil) {
        this.jwtUtil = jwtUtil;
        this.refreshService = refreshService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 다중 토큰 발급 (Access/Refresh)
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String nickname = customUserDetails.getName();
        String email = customUserDetails.getEmail();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String access = jwtUtil.createJWT("access", nickname, email, role, 600000L);
        String refresh = jwtUtil.createJWT("refresh", nickname, email, role, 86400000L);

        refreshService.addRefreshEntity(refresh, email, 86400000L);

        response.setHeader("access", access);
        response.addCookie(cookieUtil.createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect("http://localhost:8080/welcome");
    }
}
