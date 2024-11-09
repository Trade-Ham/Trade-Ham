package com.trade_ham.domain.notifiaction.repository;

import com.trade_ham.domain.notifiaction.dto.LockerNotificationDTO;
import com.trade_ham.domain.notifiaction.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<LockerNotificationDTO> findByUserId(Long userId);
    List<NotificationEntity> findByUserIdAndIsReadFalse(Long userId);
}
