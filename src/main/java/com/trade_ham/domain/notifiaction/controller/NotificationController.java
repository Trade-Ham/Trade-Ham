package com.trade_ham.domain.notifiaction.controller;

import com.trade_ham.domain.auth.dto.CustomOAuth2User;
import com.trade_ham.domain.notifiaction.dto.LockerNotificationDTO;
import com.trade_ham.domain.notifiaction.service.NotificationService;
import com.trade_ham.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/all")
    ApiResponse<List<LockerNotificationDTO>> getMyNotifications(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ApiResponse.success(notificationService.getMyNotifications(oAuth2User.getId()));
    }

    @PostMapping("/readCheck")
    ApiResponse<String> checkReadNotification(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        notificationService.checkReadNotification(oAuth2User.getId());
        return ApiResponse.success("알림을 확인 후 닫습니다.");
    }


}
