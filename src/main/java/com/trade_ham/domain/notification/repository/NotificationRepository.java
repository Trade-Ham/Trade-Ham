package com.trade_ham.domain.notification.repository;

import com.trade_ham.domain.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUser_IdOrderByCreatedAtDesc(Long userId);
}

