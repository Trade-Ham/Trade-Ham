package com.example.shoppingmallproject.login.service;

import com.example.shoppingmallproject.login.domain.ProviderType;
import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.dto.CustomOAuth2User;
import com.example.shoppingmallproject.login.dto.OAuth2Response;
import com.example.shoppingmallproject.login.dto.UserDTO;
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
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * 사용자가 OAuth 2.0 공급자에서 인증을 마친 후, 해당 사용자의 정보를 가져오는 역할
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("kakao")) {
            // 사용자 정보를 가져옴
            oAuth2Response = new OAuth2Response(oAuth2User.getAttributes(), "USER");
        } else {
            throw new OAuth2AuthenticationException("잘못된 registration id 입니다.");
        }

//        User user = userRepository.findByEmail(email)
//                .orElseGet(() -> createUser(finalEmail, finalName));
//        // 새로운 Attributes 맵 생성
//        Map<String, Object> newAttributes = Map.of(
//                "email", email,
//                "name", name
//        );

        UserDTO userDTO = new UserDTO();
        userDTO.setNickname(oAuth2Response.getNickName());
        userDTO.setEmail(oAuth2Response.getEmail());
        userDTO.setProvider(oAuth2Response.getProvider());
        userDTO.setRole(oAuth2Response.getRole());

        return new CustomOAuth2User(userDTO);
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setRole("USER");
        user.setProvider(ProviderType.KAKAO);
        return userRepository.save(user);
    }
}
