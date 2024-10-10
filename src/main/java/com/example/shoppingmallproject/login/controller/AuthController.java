package com.example.shoppingmallproject.login.controller;

import com.example.shoppingmallproject.login.dto.TokenRefreshRequest;
import com.example.shoppingmallproject.login.dto.TokenResponseDto;
import com.example.shoppingmallproject.login.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/oauth2/authorization")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 카카오 로그인 URL로 리디렉션
     */
    @GetMapping("/kakao")
    public ResponseEntity<Void> redirectKakaoLogin() {
        String kakaoLoginUrl = authService.getKakaoLoginUrl();
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(kakaoLoginUrl)).build();
    }

    /**
     * 카카오로부터 Authorization Code를 전달받아 JWT 토큰 생성
     */
    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> kakaoLoginCallback() {
        log.info("카카오 로그인 Callback 요청");

        try {
            TokenResponseDto tokenResponse = authService.createJwtTokens();
            return ResponseEntity.ok(Map.of("accessToken", tokenResponse.getAccessToken(), "refreshToken", tokenResponse.getRefreshToken()));
        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Refresh Token을 사용하여 JWT Access Token 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody TokenRefreshRequest request) {
        try {
            TokenResponseDto tokenResponse = authService.refreshJwtTokens(request.getRefreshToken());
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            log.error("토큰 갱신 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * 로그아웃 처리
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody TokenRefreshRequest request) {
        try {
            authService.invalidateRefreshToken(request.getRefreshToken());
            return ResponseEntity.ok("로그아웃 성공");
        } catch (Exception e) {
            log.error("로그아웃 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 실패");
        }
    }
}
