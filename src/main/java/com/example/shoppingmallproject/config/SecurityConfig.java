package com.example.shoppingmallproject.config;

import com.example.shoppingmallproject.login.controller.AuthController;
import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.dto.TokenResponseDto;
import com.example.shoppingmallproject.login.oauth2.CustomSuccessHandler;
import com.example.shoppingmallproject.login.security.JwtAuthenticationFilter;
import com.example.shoppingmallproject.login.security.JwtTokenProvider;
import com.example.shoppingmallproject.login.service.AuthService;
import com.example.shoppingmallproject.login.service.CustomOAuth2UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final AuthService authService;
    private final CustomSuccessHandler customSuccessHandler;

    /**
     * 애플리케이션의 보안 정책을 정의하고 필터 체인을 구성
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //csrf disable
                .formLogin(AbstractHttpConfigurer::disable) //form 로그인 방식 disable
                .httpBasic(AbstractHttpConfigurer::disable) //http basic 인증 방식 disable
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/index", "/login", "/oauth2/**").permitAll()  // 인증 없이 접근 가능한 경로
                        .anyRequest().authenticated() // 그 외의 요청은 인증이 필요함
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)
                        )
//                        .loginPage("/login")  // 로그인 페이지 경로 설정
//                        .loginProcessingUrl("/oauth2/authorization/callback/kakao")
                        .successHandler(customAuthenticationSuccessHandler())  // Custom Success Handler 추가
//                        .failureUrl("/loginFailure")
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless로 세션 설정
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Custom AuthenticationSuccessHandler Bean 정의
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // JWT 토큰 생성
            TokenResponseDto tokenResponse = authService.createJwtTokens();

            User user = tokenResponse.getUserInfo();
            // User 객체를 JSON으로 직렬화
            ObjectMapper objectMapper = new ObjectMapper();
            String userInfoJson = objectMapper.writeValueAsString(user);

            // URL에 포함할 각 값을 UTF-8로 인코딩
            String encodedAccessToken = URLEncoder.encode(tokenResponse.getAccessToken(), StandardCharsets.UTF_8.toString());
            String encodedUserInfo = URLEncoder.encode(userInfoJson, StandardCharsets.UTF_8.toString());

            // 리다이렉트할 URL 생성
            String redirectUrl = String.format("http://localhost:8080/?accessToken=%s&userInfo=%s", encodedAccessToken, encodedUserInfo);

            // 리다이렉트 처리
            response.sendRedirect(redirectUrl);
        };
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }
}
