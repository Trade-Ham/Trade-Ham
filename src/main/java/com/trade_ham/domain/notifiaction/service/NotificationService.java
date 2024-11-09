package com.trade_ham.domain.notifiaction.service;

import com.trade_ham.domain.notifiaction.dto.LockerNotificationDTO;
import com.trade_ham.domain.notifiaction.entity.NotificationEntity;
import com.trade_ham.domain.notifiaction.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<LockerNotificationDTO> getMyNotifications(Long userId) {
        List<LockerNotificationDTO> notifications = notificationRepository.findByUserId(userId);
        if (notifications == null || notifications.isEmpty()) {
            return Collections.emptyList();
        }
        return notifications;
    }

    @Transactional
    public List<NotificationEntity> checkReadNotification(Long userId) {
        List<NotificationEntity> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        if (notifications == null || notifications.isEmpty()) {
            return Collections.emptyList();
        }
        notifications.forEach(notification -> notification.setRead(true));
        return notificationRepository.saveAll(notifications);
    }
}
