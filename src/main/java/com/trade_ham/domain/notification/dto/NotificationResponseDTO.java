package com.trade_ham.domain.notification.dto;


import com.trade_ham.domain.notification.entity.NotificationEntity;
import com.trade_ham.domain.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDTO {
    private Long id;
    private String message;
    private NotificationType type;
    private String additionalData;
    private LocalDateTime createdAt;
    private boolean isRead;

    public static NotificationResponseDTO from(NotificationEntity entity) {
        return NotificationResponseDTO.builder()
                .id(entity.getNotificationId())
                .message(entity.getMessage())
                .type(entity.getType())
                .additionalData(entity.getAdditionalData())
                .createdAt(entity.getCreatedAt())
                .isRead(entity.getIsRead())
                .build();
    }
}