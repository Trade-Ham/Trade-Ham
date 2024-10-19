    package com.example.shoppingmallproject.config;

    import com.example.shoppingmallproject.user.domain.User;
    import com.example.shoppingmallproject.user.oauth2.OAuth2LogoutSuccessHandler;
    import com.example.shoppingmallproject.user.jwt.JwtAuthenticationFilter;
    import com.example.shoppingmallproject.user.jwt.JwtTokenProvider;
    import com.example.shoppingmallproject.user.oauth2.OAuth2LoginSuccessHandler;
    import com.example.shoppingmallproject.user.repository.UserRepository;
    import com.example.shoppingmallproject.user.oauth2.CustomOAuth2UserService;

    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    @Configuration
    @EnableWebSecurity
    @RequiredArgsConstructor
    @Slf4j
    public class SecurityConfig {

        private final JwtTokenProvider jwtTokenProvider;
        private final CustomOAuth2UserService customOAuth2UserService;
        private final UserDetailsService userDetailsService;
        private final UserRepository userRepository;
        private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
        private final OAuth2LogoutSuccessHandler OAuth2LogoutSuccessHandler;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(sessionManagement ->
                            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    )
                    .authorizeHttpRequests(authz -> authz
                            .requestMatchers("/", "/auth/**", "/oauth2/**", "/logout").permitAll()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/auth/login")
                            .loginProcessingUrl("/auth/admin-login-process")
                            .usernameParameter("email")  // This should match the form field name
                            .passwordParameter("password")
                            .successHandler((request, response, authentication) -> {
                                log.info("Admin login successful: {}", authentication.getName());
                                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                                // 이메일로 사용자 조회
                                User user = userRepository.findByEmail(userDetails.getUsername())
                                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                                // 사용자 ID 추출
                                Long userId = user.getId();

                                // 사용자 ID로 액세스 토큰 생성
                                String token = jwtTokenProvider.createAccessToken(userId);                            response.setHeader("Authorization", "Bearer " + token);
                                response.sendRedirect("/admin/home");
                            })
                            .failureHandler((request, response, exception) -> {
                                log.error("Admin Login Failed", exception);
                                response.sendRedirect("/auth/admin-login?error");
                            })
                            .permitAll()
                    )
                    .oauth2Login(oauth2 -> oauth2
                            .loginPage("/auth/login")
                            .userInfoEndpoint(userInfo -> userInfo
                                    .userService(customOAuth2UserService)
                            )
                            .successHandler(oAuth2LoginSuccessHandler)
                            .failureHandler((request, response, exception) -> {
                                log.error("OAuth2 Login Failed", exception);
                                response.sendRedirect("/auth/login?error");
                            })
                    )
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutSuccessHandler(OAuth2LogoutSuccessHandler)
                            .clearAuthentication(true)
                            .invalidateHttpSession(true))
                    .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
            return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService);
            authProvider.setPasswordEncoder(passwordEncoder());
            return authProvider;
        }

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
