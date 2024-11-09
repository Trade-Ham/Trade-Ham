package com.trade_ham.domain.locker.controller;

import com.trade_ham.domain.auth.dto.CustomOAuth2User;
import com.trade_ham.domain.locker.entity.NotificationEntity;
import com.trade_ham.domain.locker.service.NotificationService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping()
    public ApiResponse<List<NotificationEntity>> allNotification(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        Long userId = oAuth2User.getId();

        List<NotificationEntity> notifications = notificationService.allNotification(userId);
        return ApiResponse.success(notifications);
    }
}
