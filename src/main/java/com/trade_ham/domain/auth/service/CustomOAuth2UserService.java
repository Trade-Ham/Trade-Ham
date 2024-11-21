package com.trade_ham.domain.auth.service;



import com.trade_ham.domain.auth.dto.*;
import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.auth.repository.UserRepository;
import com.trade_ham.global.common.enums.Provider;
import com.trade_ham.global.common.enums.Role;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest); // 유저 정보
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        UserEntity existData = userRepository.findByProviderAndEmail(oAuth2Response.getProvider(), oAuth2Response.getEmail());

        if (existData == null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(oAuth2Response.getEmail()); // ex) tiger1650@naver.com
            userEntity.setProvider(oAuth2Response.getProvider()); // KAKAO
            userEntity.setNickname(oAuth2Response.getNickName()); // ex) 이용우
            userEntity.setProfile_image(oAuth2Response.getProfileImage()); // ex) 프로필 이미지
            userEntity.setRole(Role.USER);

            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();

            userDTO.setId(userRepository.findByProviderAndEmail(oAuth2Response.getProvider(), oAuth2Response.getEmail()).getId());
            userDTO.setEmail(oAuth2Response.getEmail());
            userDTO.setNickname(oAuth2Response.getNickName());
            userDTO.setRole(Role.USER);

            return new CustomOAuth2User(userDTO);

        }
        else{ // 이미 존재한다면
            UserDTO userDTO = new UserDTO();
            userDTO.setId(existData.getId());
            userDTO.setNickname(existData.getNickname());
            userDTO.setEmail(existData.getEmail());
            userDTO.setRole(existData.getRole());

            return new CustomOAuth2User(userDTO);
        }

    }

}
