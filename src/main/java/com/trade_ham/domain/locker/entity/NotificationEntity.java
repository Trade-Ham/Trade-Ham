package com.trade_ham.domain.locker.entity;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.locker.dto.NotificationBuyerDTO;
import com.trade_ham.domain.locker.dto.NotificationLockerDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    //seller
    @Column(name = "locker_id")
    private Long lockerId;

    @Column(name = "locker_password")
    private String lockerPassword;

    // common
    private String message;

    @Setter
    private boolean isRead;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 판매자 전용 알림
    public NotificationEntity(NotificationLockerDTO notificationLockerDTO) {
        this.message = notificationLockerDTO.getMessage();
        this.lockerId = notificationLockerDTO.getLockerId();
        this.lockerPassword = notificationLockerDTO.getLockerPassword();
        this.isRead = notificationLockerDTO.isRead();
        this.user = notificationLockerDTO.getUserId();
    }

    // 구매자 전용 알림
    public NotificationEntity(NotificationBuyerDTO NotificationBuyerDTO) {
        this.message = NotificationBuyerDTO.getMessage();
        this.isRead = NotificationBuyerDTO.isRead();
        this.user = NotificationBuyerDTO.getUserId();
    }
}
