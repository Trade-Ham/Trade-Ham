package com.example.shoppingmallproject.login.service;

import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.dto.TokenResponseDto;
import com.example.shoppingmallproject.login.repository.UserRepository;
import com.example.shoppingmallproject.login.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${security.oauth2.client.provider.kakao.authorization-uri}")
    private String kakaoAuthBaseUrl;

    /**
     * 카카오 로그인 URL 생성
     */
    public String getKakaoLoginUrl() {
        return kakaoAuthBaseUrl + "?response_type=code&client_id="
                + kakaoClientId + "&redirect_uri=" + kakaoRedirectUri;
    }

    /**
     * 현재 인증된 사용자를 기준으로 JWT Access Token 및 Refresh Token 생성
     */
    public TokenResponseDto createJwtTokens() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;
            String email = (String) oAuth2User.getAttributes().get("email");

            // 이메일을 기준으로 사용자 정보 조회
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
            }

            User user = userOptional.get();
            log.info("사용자 정보: {} - JWT 생성 시작", user.getEmail());

            // JWT Access Token 및 Refresh Token 생성
            Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
            String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), authorities);
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

            return new TokenResponseDto(accessToken, refreshToken);
        } else {
            throw new RuntimeException("OAuth2 사용자 정보가 없습니다.");
        }
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token 발급
     */
    public TokenResponseDto refreshJwtTokens(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        String email = jwtTokenProvider.getUsername(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(email, jwtTokenProvider.getRoles(refreshToken));

        return new TokenResponseDto(newAccessToken, refreshToken);
    }

    /**
     * 사용자의 Refresh Token 무효화 (로그아웃 처리)
     */
    public void invalidateRefreshToken(String refreshToken) {
        log.info("Refresh Token 무효화 처리: {}", refreshToken);
        // 필요 시, Refresh Token을 DB나 캐시에서 삭제하는 로직 추가
    }
}
