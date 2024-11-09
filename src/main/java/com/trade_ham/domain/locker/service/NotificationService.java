package com.trade_ham.domain.locker.service;

import com.trade_ham.domain.locker.dto.NotificationResponseDTO;
import com.trade_ham.domain.locker.entity.NotificationEntity;
import com.trade_ham.domain.locker.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public List<NotificationEntity> allNotification(Long userId) {
        List<NotificationEntity> notifications = notificationRepository.findByUserIdAndIsReadTrue(userId);

        for(NotificationEntity notificationEntity : notifications) {
            notificationEntity.setRead(false);
        }

        return notificationRepository.saveAll(notifications);
    }
}
