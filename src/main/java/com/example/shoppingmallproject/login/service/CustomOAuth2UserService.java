package com.example.shoppingmallproject.login.service;

import com.example.shoppingmallproject.login.domain.ProviderType;
import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    /**
     * 사용자가 OAuth 2.0 공급자에서 인증을 마친 후, 해당 사용자의 정보를 가져오는 역할
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser");
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 사용자 정보를 가져옴
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Kakao의 경우 'kakao_account'라는 키 안에 email 정보가 있음
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = null;
        String name = null;

        if (kakaoAccount != null) {
            email = (String) kakaoAccount.get("email");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                name = (String) profile.get("nickname");
            }
        }

        // 이메일 정보가 없는 경우 예외 처리
        if (email == null) {
            throw new IllegalArgumentException("이메일 정보가 없습니다. 이메일 제공에 동의했는지 확인하세요.");
        }

        // 사용자 정보를 데이터베이스에 저장하거나 기존 사용자 조회
        String finalEmail = email;
        String finalName = name;
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(finalEmail, finalName));
        // 새로운 Attributes 맵 생성
        Map<String, Object> newAttributes = Map.of(
                "email", email,
                "name", name
        );

        // DefaultOAuth2User를 생성할 때 newAttributes를 전달
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                newAttributes,
                "email"
        );
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setRole("ROLE_USER");
        user.setProvider(ProviderType.KAKAO);
        return userRepository.save(user);
    }
}
