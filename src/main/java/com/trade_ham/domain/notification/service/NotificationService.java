package com.trade_ham.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.notification.dto.NotificationResponseDTO;
import com.trade_ham.domain.notification.entity.NotificationEntity;
import com.trade_ham.domain.notification.entity.NotificationType;
import com.trade_ham.domain.notification.repository.NotificationRepository;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.exception.NotificationNotCreateException;
import com.trade_ham.global.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void createLockerNotification(UserEntity seller, String lockerId, String password) {
        Map<String, String> lockerInfo = new HashMap<>();
        lockerInfo.put("lockerId", lockerId);
        lockerInfo.put("password", password);

        try {
            NotificationEntity notification = NotificationEntity.builder()
                    .user(seller)
                    .message("새로운 사물함이 할당되었습니다.")
                    .type(NotificationType.LOCKER_INFO)
                    .additionalData(objectMapper.writeValueAsString(lockerInfo))
                    .build();

            notificationRepository.save(notification);
        } catch (Exception e) {
            throw new NotificationNotCreateException(ErrorCode.NOTIFICATION_CREATION_ERROR);
        }
    }

    @Transactional
    public void createPurchaseCompleteNotification(UserEntity buyer, String productName) {
        NotificationEntity notification = NotificationEntity.builder()
                .user(buyer)
                .message(String.format("'%s' 상품 구매가 완료되었습니다.", productName))
                .type(NotificationType.PURCHASE_COMPLETE)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        List<NotificationEntity> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationResponseDTO::from)
                .toList();
    }

    @Transactional
    public Long changeNotificationStatus(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        notification.markAsRead();

        return notification.getNotificationId();
    }
}

