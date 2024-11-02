//package com.example.shoppingmallproject.login.controller;
//
//import com.example.shoppingmallproject.login.dto.TokenResponseDto;
//import com.example.shoppingmallproject.login.service.AuthService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//
//import static org.hamcrest.Matchers.containsString;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class AuthControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private AuthService authService;
//
//    @BeforeEach
//    public void setUp() {
//        // Mock 데이터 설정
//        TokenResponseDto mockResponse = new TokenResponseDto("mockAccessToken", "mockRefreshToken");
//        Mockito.when(authService.createJwtTokens()).thenReturn(mockResponse);
//    }
//
//    /**
//     * 카카오 로그인 리디렉션 URL에서 code 및 state 파라미터를 추출하여 테스트 메서드 변수로 저장.
//     */
//    @Test
//    public void testRedirectKakaoLogin_Success() throws Exception {
//        // Perform the request and capture the result
//        MvcResult result = mockMvc.perform(get("/oauth2/authorization/kakao"))  // 로그인 URL로 리디렉션
//                .andExpect(status().is3xxRedirection())  // 리다이렉트 상태 코드 확인
//                .andExpect(header().string("Location", containsString("https://kauth.kakao.com/oauth/authorize")))  // 리다이렉트 URL 확인
//                .andReturn();  // MvcResult 객체를 반환하여 결과를 캡처
//
//        // 리다이렉션된 URL을 가져와 출력 및 파라미터 추출
//        String redirectedUrl = result.getResponse().getHeader("Location");
//        System.out.println("Redirected URL: " + redirectedUrl);
//    }
//
//    /**
//     * 카카오 로그인 callback URL로 요청을 보낸 후 JWT 토큰이 올바르게 생성되는지 확인.
//     */
//    @Test
//    void kakaoLoginCallback() throws Exception {
//        // given
//        String code = "sampleCode";
//        String state = "sampleState";
//        String expectedResponse = "ddd";
//
//
//        // when
//        ResultActions resultActions = mockMvc.perform(get("/oauth2/authorization/kakao/callback")
//                .param("code", code)
//                .param("state", state)
//                .contentType(MediaType.APPLICATION_JSON));
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(content().string(expectedResponse)) // "ddd"를 반환하는지 확인
//                .andDo(print());
//    }
//}
