package com.example.shoppingmallproject.login.service;

import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.dto.TokenResponseDto;
import com.example.shoppingmallproject.login.repository.UserRepository;
import com.example.shoppingmallproject.login.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            throw new RuntimeException("OAuth2 사용자 정보가 없습니다.");
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }

        User user = userOptional.get();

        // JWT Access Token 및 Refresh Token 생성
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), authorities);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        return new TokenResponseDto(accessToken, refreshToken, user);
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

        return new TokenResponseDto(newAccessToken, refreshToken, null);
    }

    /**
     * 사용자의 Refresh Token 무효화 (로그아웃 처리)
     */
    public void invalidateRefreshToken(String refreshToken) {
        log.info("Refresh Token 무효화 처리: {}", refreshToken);
        // 필요 시, Refresh Token을 DB나 캐시에서 삭제하는 로직 추가
    }
}
