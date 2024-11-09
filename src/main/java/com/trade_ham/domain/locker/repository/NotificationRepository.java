package com.trade_ham.domain.locker.repository;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.locker.dto.NotificationResponseDTO;
import com.trade_ham.domain.locker.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserIdAndIsReadTrue(Long userId);
}
