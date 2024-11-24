package com.trade_ham.domain.notification.repository;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUser_IdOrderByCreatedAtDesc(Long userId);
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.user = :user")
    void markAllAsReadByUser(UserEntity user);
}

