package com.example.shoppingmallproject.service;

import com.example.shoppingmallproject.cookie.CookieUtil;
import com.example.shoppingmallproject.entity.Refresh;
import com.example.shoppingmallproject.jwt.JWTUtil;
import com.example.shoppingmallproject.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshService {

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshRepository refreshRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public RefreshService(JWTUtil jwtUtil, CookieUtil cookieUtil, RefreshRepository refreshRepository, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.refreshRepository = refreshRepository;
        this.redisTemplate = redisTemplate;
    }

    public void saveRefreshToken(Refresh refresh, Long expiredMs) {
        //Refresh 엔티티를 redis에 저장
        refreshRepository.save(refresh);

        //TTL 설정
        redisTemplate.expire("KTB.BackendStudy.entity.Refresh:" + refresh.getRefresh(), expiredMs / 1000, TimeUnit.SECONDS);
//        redisTemplate.expire("KTB.BackendStudy.entity.Refresh:eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJ1c2VybmFtZSI6IuuwleywrOyYgSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzI4NDU4OTExLCJleHAiOjE3Mjg1NDUzMTF9.zQvcX3I04VpQWvAdQbzHA_VRLKlWgPLSNb_4xh3782E", 3600, TimeUnit.SECONDS);
    }

    public void addRefreshEntity(String refresh, String email, Long expiredMs) {

        Refresh refreshEntity = new Refresh();
        refreshEntity.setRefresh(refresh);
        refreshEntity.setEmail(email);

        saveRefreshToken(refreshEntity, expiredMs);
    }

    public Boolean existsByRefresh(String refresh) {
        return refreshRepository.existsById(refresh);
    }

    public void deleteByRefresh(String refresh) {
        refreshRepository.deleteById(refresh);
    }

    public ResponseEntity<?> refreshRotate(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        Boolean isExist = existsByRefresh(refresh);
        if (!isExist) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String nickname = jwtUtil.getNickname(refresh);
        String email = jwtUtil.getEmail(refresh);
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJWT("access", nickname, email, role, 600000L);
        String newRefresh = jwtUtil.createJWT("refresh", nickname, email, role, 86400000L);

        deleteByRefresh(refresh);
        addRefreshEntity(newRefresh, email, 86400000L);


        response.setHeader("access", newAccess);
        response.addCookie(cookieUtil.createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
