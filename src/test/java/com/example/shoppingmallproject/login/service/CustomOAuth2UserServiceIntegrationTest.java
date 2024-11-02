//package com.example.shoppingmallproject.login.service;
//
//import com.example.shoppingmallproject.login.domain.User;
//import com.example.shoppingmallproject.login.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Collections;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest(properties = {
//        "security.oauth2.client.registration.kakao.client-id=c6f73d1413ce98f2b15bb1f0b503e269",
//        "security.oauth2.client.registration.kakao.client-secret=9gy4fOWLsmultqsajUHTALKm0oB3Ne0L",
//        "security.oauth2.client.registration.kakao.redirect-uri=http://localhost:8080/oauth2/authorization/callback/kakao",
//        "security.oauth2.client.provider.kakao.authorization-uri=https://kauth.kakao.com/oauth/authorize",
//        "security.oauth2.client.provider.kakao.token-uri=https://kauth.kakao.com/oauth/token",
//        "security.oauth2.client.provider.kakao.user-info-uri=https://kapi.kakao.com/v2/user/me",
//        "security.oauth2.client.provider.kakao.user-name-attribute=id"
//})
//@Transactional
//class CustomOAuth2UserServiceIntegrationTest {
//
//    @Autowired
//    private CustomOAuth2UserService customOAuth2UserService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @MockBean
//    private OAuth2UserService<OAuth2UserRequest, OAuth2User> defaultOAuth2UserService;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    private OAuth2UserRequest mockUserRequest;
//
//    @BeforeEach
//    public void setUp() {
//        // 사용자 요청에 대한 OAuth2UserRequest 객체를 생성하여 테스트에 사용할 수 있도록 설정
//        // 실제 애플리케이션 설정에 맞게 mockUserRequest를 생성해주어야 합니다.
//        mockUserRequest = Mockito.mock(OAuth2UserRequest.class);
//
//        // Mock OAuth2User 설정
//        Map<String, Object> attributes = Map.of(
//                "email", "testuser@example.com",
//                "name", "Test User"
//        );
//        OAuth2User mockOAuth2User = new DefaultOAuth2User(
//                Collections.singleton(() -> "ROLE_USER"),
//                attributes,
//                "email"
//        );
//
//        // Mock 기본 OAuth2UserService가 해당 사용자 정보를 반환하도록 설정
//        when(defaultOAuth2UserService.loadUser(any(OAuth2UserRequest.class))).thenReturn(mockOAuth2User);
//    }
//
//    @Test
//    public void testLoadUserAndSaveToDatabase() {
//        // Given: 데이터베이스에 사용자가 없는 상태
//        assertThat(userRepository.findByEmail("testuser@example.com")).isEmpty();
//
//        // When: loadUser 메서드를 호출하여 사용자 정보를 저장하고 로드
//        OAuth2User loadedUser = customOAuth2UserService.loadUser(mockUserRequest);
//
//        // Then: 데이터베이스에 사용자가 잘 저장되었는지 확인
//        Optional<User> savedUser = userRepository.findByEmail("testuser@example.com");
//        assertThat(savedUser).isPresent();
//        assertThat(savedUser.get().getEmail()).isEqualTo("testuser@example.com");
//        assertThat(savedUser.get().getName()).isEqualTo("Test User");
//
//        // 또한, loadUser 메서드의 반환값이 예상한 OAuth2User 객체인지 검증
//        assertThat((String) loadedUser.getAttribute("email")).isEqualTo("testuser@example.com");
//    }
//
//    @Test
//    public void testLoadUser_ExistingUser() {
//        // Given: 데이터베이스에 이미 사용자가 있는 상태
//        User existingUser = new User();
//        existingUser.setEmail("existinguser@example.com");
//        existingUser.setName("Existing User");
//        userRepository.save(existingUser);
//
//        // When: 기존 사용자를 로드하여 정보가 변경되지 않음을 확인
//        OAuth2User loadedUser = customOAuth2UserService.loadUser(mockUserRequest);
//
//        // Then: 데이터베이스에 기존 사용자 정보가 그대로 유지되는지 확인
//        Optional<User> savedUser = userRepository.findByEmail("existinguser@example.com");
//        assertThat(savedUser).isPresent();
//        assertThat(savedUser.get().getEmail()).isEqualTo("existinguser@example.com");
//        assertThat(savedUser.get().getName()).isEqualTo("Existing User");
//    }
//}
