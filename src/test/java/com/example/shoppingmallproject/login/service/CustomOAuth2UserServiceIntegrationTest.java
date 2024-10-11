package com.example.shoppingmallproject.login.service;

import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.security.oauth2.client.registration.kakao.client-id=test-client-id",
        "spring.security.oauth2.client.registration.kakao.client-secret=test-client-secret",
        "spring.security.oauth2.client.registration.kakao.redirect-uri=http://localhost:8080/login/oauth2/code/kakao",
        "spring.security.oauth2.client.registration.kakao.authorization-grant-type=authorization_code",
        "spring.security.oauth2.client.registration.kakao.scope=profile_nickname",
        "spring.security.oauth2.client.registration.kakao.client-name=Kakao",
        "spring.security.oauth2.client.provider.kakao.authorization-uri=https://kauth.kakao.com/oauth/authorize",
        "spring.security.oauth2.client.provider.kakao.token-uri=https://kauth.kakao.com/oauth/token",
        "spring.security.oauth2.client.provider.kakao.user-info-uri=https://kapi.kakao.com/v2/user/me",
        "spring.security.oauth2.client.provider.kakao.user-name-attribute=id"
})
class CustomOAuth2UserServiceTest {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DefaultOAuth2UserService defaultOAuth2UserService; // DefaultOAuth2UserService를 Mock으로 등록

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    private OAuth2UserRequest userRequest;

    @BeforeEach
    void setUp() {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("kakao");

        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "access-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        userRequest = new OAuth2UserRequest(clientRegistration, oAuth2AccessToken);

        // DefaultOAuth2UserService의 loadUser 메서드를 모킹하여 가짜 OAuth2User 반환
        Map<String, Object> attributes = Map.of(
                "id", "12345",
                "email", "test@example.com",
                "name", "Test User"
        );
        OAuth2User mockOAuth2User = new DefaultOAuth2User(
                Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"
        );

        when(defaultOAuth2UserService.loadUser(userRequest)).thenReturn(mockOAuth2User);
    }

    @Test
    void testUserSavedToDatabaseOnLogin() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        OAuth2User oAuth2User = customOAuth2UserService.loadUser(userRequest);

        assertThat(oAuth2User).isNotNull();
        assertThat((String) oAuth2User.getAttribute("email")).isEqualTo("test@example.com");
        assertThat((String) oAuth2User.getAttribute("name")).isEqualTo("Test User");

        verify(userRepository).save(any(User.class));
    }
}
