package com.trade_ham.domain.auth.controller;

import com.trade_ham.domain.auth.dto.CustomOAuth2User;
import com.trade_ham.domain.auth.dto.UserUpdateDTO;
import com.trade_ham.domain.auth.service.UserService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public ApiResponse<UserUpdateDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO, @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        Long sellerId = oAuth2User.getId();
        // ID가 제대로 나오는지 확인
        log.info("OAuth2 User ID: {}", oAuth2User.getId());

        UserUpdateDTO userResponse = userService.updateUser(sellerId, userUpdateDTO);

        return ApiResponse.success(userResponse);
    }
}
