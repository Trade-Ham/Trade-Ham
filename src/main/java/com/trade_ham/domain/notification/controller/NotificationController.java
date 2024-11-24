package com.trade_ham.domain.notification.controller;

import com.trade_ham.domain.auth.dto.CustomOAuth2User;
import com.trade_ham.domain.notification.dto.NotificationResponseDTO;
import com.trade_ham.domain.notification.service.NotificationService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ApiResponse<List<NotificationResponseDTO>> getUserNotifications(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    ) {
        List<NotificationResponseDTO> notifications = notificationService.getUserNotifications(oAuth2User.getId());
        return ApiResponse.success(notifications);
    }
}
