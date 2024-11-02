//package com.example.shoppingmallproject.login.service;
//
//import com.example.shoppingmallproject.login.domain.User;
//import com.example.shoppingmallproject.login.dto.TokenResponseDto;
//import com.example.shoppingmallproject.login.repository.UserRepository;
//import com.example.shoppingmallproject.login.security.JwtTokenProvider;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.anyCollection;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//class AuthServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private JwtTokenProvider jwtTokenProvider;
//
//    @InjectMocks
//    private AuthService authService;
//
//    @BeforeEach
//    public void setUp() {
//        // Mockito 초기화
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testCreateJwtTokens() {
//        // Mock 사용자 정보 설정
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("email", "test@example.com");
//
//        OAuth2UserAuthority authority = new OAuth2UserAuthority(attributes);
//        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.singleton(authority), attributes, "email");
//
//        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(oAuth2User, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))));
//
//        User mockUser = new User();
//        mockUser.setEmail("test@example.com");
//        mockUser.setRole("ROLE_USER");
//
//        // Mock 데이터 설정
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
//        when(jwtTokenProvider.createAccessToken(anyString(), anyCollection())).thenReturn("mockAccessToken");
//        when(jwtTokenProvider.createRefreshToken(anyString())).thenReturn("mockRefreshToken");
//
//        // 테스트 실행
//        TokenResponseDto response = authService.createJwtTokens();
//
//        // 검증
//        assertThat(response).isNotNull();
//        assertThat(response.getAccessToken()).isEqualTo("mockAccessToken");
//        assertThat(response.getRefreshToken()).isEqualTo("mockRefreshToken");
//
//        verify(userRepository).findByEmail("test@example.com");
//        verify(jwtTokenProvider).createAccessToken(eq("test@example.com"), anyCollection());
//        verify(jwtTokenProvider).createRefreshToken(eq("test@example.com"));
//    }
//
//}
