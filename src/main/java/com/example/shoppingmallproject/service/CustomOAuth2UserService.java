package com.example.shoppingmallproject.service;

import com.example.shoppingmallproject.dto.CustomOAuth2User;
import com.example.shoppingmallproject.dto.KakaoResponse;
import com.example.shoppingmallproject.dto.OAuth2Response;
import com.example.shoppingmallproject.dto.UserDTO;
import com.example.shoppingmallproject.entity.Provider;
import com.example.shoppingmallproject.entity.Role;
import com.example.shoppingmallproject.entity.User;
import com.example.shoppingmallproject.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //소셜 로그인(카카오)을 통해 발급된 엑세스 토큰을 사용하여 리소스 서버(카카오)로부터 사용자 정보를 가져오는 역할
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User = " + oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        User existUser = userRepository.findByEmail(oAuth2Response.getEmail());

        Role userRole = Role.USER; // 기본 역할
        if ("uniti0903@naver.com".equals(oAuth2Response.getEmail())) {
            userRole = Role.ADMIN; // 특정 이메일에 대해 ADMIN 역할 부여
        }

        if (existUser == null) {

            User user = new User();
            user.setNickname(oAuth2Response.getNickname());
            user.setEmail(oAuth2Response.getEmail());
            user.setProfileImage(oAuth2Response.getProfileImage());
            user.setRole(userRole);
            user.setProvider(Provider.KAKAO);

            Timestamp now = new Timestamp(System.currentTimeMillis());
            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            userRepository.save(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setNickname(oAuth2Response.getNickname());
            userDTO.setEmail(oAuth2Response.getEmail());
            userDTO.setProfileImage(oAuth2Response.getProfileImage());
            userDTO.setRole(userRole);

            return new CustomOAuth2User(userDTO);

        } else {

            existUser.setNickname(oAuth2Response.getNickname());
            existUser.setEmail(oAuth2Response.getEmail());
            existUser.setProfileImage(oAuth2Response.getProfileImage());
            existUser.setRole(userRole);

            Timestamp now = new Timestamp(System.currentTimeMillis());
            existUser.setUpdatedAt(now);

            userRepository.save(existUser);

            UserDTO userDTO = new UserDTO();
            userDTO.setNickname(oAuth2Response.getNickname());
            userDTO.setEmail(oAuth2Response.getEmail());
            userDTO.setProfileImage(oAuth2Response.getProfileImage());
            userDTO.setRole(userRole);

            return new CustomOAuth2User(userDTO);
        }
    }
}
