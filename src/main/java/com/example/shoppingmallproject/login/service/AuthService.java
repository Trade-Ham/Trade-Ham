package com.example.shoppingmallproject.login.service;

import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.dto.CustomOAuth2User;
import com.example.shoppingmallproject.login.dto.TokenResponseDto;
import com.example.shoppingmallproject.login.dto.UpdateUserInfoRequest;
import com.example.shoppingmallproject.login.repository.UserRepository;
import com.example.shoppingmallproject.login.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
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

        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            throw new RuntimeException("OAuth2 사용자 정보가 없습니다.");
        }
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = customUserDetails.getId();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }

        User user = userOptional.get();

        // JWT Access Token 및 Refresh Token 생성
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), authorities);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return new TokenResponseDto(accessToken, refreshToken, user);
    }


    /**
     * Refresh Token을 사용하여 새로운 Access Token 발급
     */
    public TokenResponseDto refreshJwtTokens(HttpServletResponse response, String refreshToken) {
        String userId = redisService.getUserIdByRefreshToken(refreshToken);
        // Refresh Token이 Redis에 존재하는지 확인
        String storedRefreshToken = redisService.getRefreshToken(userId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            //refresh token도 재생성 -> 하면 안됨 -> 탈취된 토큰을 가져왔을 때 재생성됨 -> 로그인창으로 보내서 새로 로그인하게 만들게 하면 refresh token은 알아서 생성됨
            if (!storedRefreshToken.equals(refreshToken)) {
                redisService.deleteRefreshToken(storedRefreshToken);
            }
            throw new IllegalArgumentException("Refresh Token이 유효하지 않거나 만료되었습니다.");
        }
        String newAccessToken = jwtTokenProvider.createAccessToken(Long.parseLong(userId), jwtTokenProvider.getRoles(refreshToken));
        String newRefreshToken = jwtTokenProvider.createRefreshToken(Long.parseLong(userId));

        redisService.deleteRefreshToken(storedRefreshToken);
        redisService.saveRefreshToken(userId, newRefreshToken, Duration.ofDays(7));

        response.setHeader("access", newAccessToken);
        response.addCookie(createCookie("refresh", newRefreshToken, Duration.ofDays(7)));
        return new TokenResponseDto(newAccessToken, newRefreshToken, null);
    }

    public void updateUserInfo(String accessToken, UpdateUserInfoRequest request) {
        Long userId = jwtTokenProvider.getUserId(accessToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        user.setAccount(request.getAccount());
        user.setRealname(request.getRealname());
        userRepository.save(user);
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
