package com.example.shoppingmallproject.login.service;

import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.dto.CustomOAuth2User;
import com.example.shoppingmallproject.login.dto.TokenResponseDto;
import com.example.shoppingmallproject.login.dto.UpdateUserInfoRequest;
import com.example.shoppingmallproject.login.repository.UserRepository;
import com.example.shoppingmallproject.login.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

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
    public TokenResponseDto createJwtTokens(Authentication authentication) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            throw new RuntimeException("OAuth2 사용자 정보가 없습니다.");
        }
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String email = customUserDetails.getEmail();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }

        User user = userOptional.get();

        // JWT Access Token 및 Refresh Token 생성
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), authorities);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        return new TokenResponseDto(accessToken, refreshToken, user);
    }


    /**
     * Refresh Token을 사용하여 새로운 Access Token 발급
     */
    public TokenResponseDto refreshJwtTokens(String refreshToken) {
        String email = jwtTokenProvider.getUserEmail(refreshToken);
        // Refresh Token이 Redis에 존재하는지 확인
        String storedRefreshToken = redisService.getRefreshToken(email);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            //refresh token도 재생성
            throw new IllegalArgumentException("Refresh Token이 유효하지 않거나 만료되었습니다.");
        }
        String newAccessToken = jwtTokenProvider.createAccessToken(email, jwtTokenProvider.getRoles(refreshToken));

        return new TokenResponseDto(newAccessToken, refreshToken, null);
    }

    /**
     * 사용자의 Refresh Token 무효화 (로그아웃 처리)
     */
    public void invalidateRefreshToken(String refreshToken) {
        log.info("Refresh Token 무효화 처리: {}", refreshToken);
        // 필요 시, Refresh Token을 DB나 캐시에서 삭제하는 로직 추가
        // access token도 같이 삭제 처리 해줘야 함
        redisService.deleteRefreshToken(jwtTokenProvider.getUserEmail(refreshToken));
    }

    public void updateUserInfo(String accessToken, UpdateUserInfoRequest request) {
        String email = jwtTokenProvider.getUserEmail(accessToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        user.setAccount(request.getAccount());
        user.setRealname(request.getRealname());
        userRepository.save(user);
    }
}
