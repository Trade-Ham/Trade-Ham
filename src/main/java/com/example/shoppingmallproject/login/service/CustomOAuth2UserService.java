package com.example.shoppingmallproject.login.service;

import com.example.shoppingmallproject.login.domain.ProviderType;
import com.example.shoppingmallproject.login.domain.RoleType;
import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.dto.CustomOAuth2User;
import com.example.shoppingmallproject.login.dto.OAuth2Response;
import com.example.shoppingmallproject.login.dto.UserDTO;
import com.example.shoppingmallproject.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

        OAuth2Response finalOAuth2Response = oAuth2Response;
        User user = userRepository.findByEmail(oAuth2Response.getEmail())
                .orElseGet(() -> createUser(finalOAuth2Response));

        UserDTO userDTO = new UserDTO();
        userDTO.setNickname(user.getNickname());
        userDTO.setEmail(user.getEmail());
        userDTO.setProvider(user.getProvider());
        userDTO.setRole(user.getRole().toString());

        return new CustomOAuth2User(userDTO);
    }

    private User createUser(OAuth2Response oAuth2Response) {
        User user = new User();
        user.setEmail(oAuth2Response.getEmail());
        user.setNickname(oAuth2Response.getNickName());
        if(Objects.equals(oAuth2Response.getRole(), "ADMIN")) {
            user.setRole(RoleType.ADMIN);
        } else {
            user.setRole(RoleType.USER);
        }
        user.setProvider(ProviderType.KAKAO);
        user.setProfileImage(oAuth2Response.getProfileImage());
        return userRepository.save(user);
    }
}
