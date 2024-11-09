package com.trade_ham.domain.notifiaction.entity;

import com.trade_ham.domain.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Setter;

@Entity
public class NotificationEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity user;

    private String lockerId;
    private String lockerPassword;

    @Setter
    private boolean isRead;

    private NotificationEntity(UserEntity user, String lockerId, String lockerPassword) {
        this.user = user;
        this.lockerId = lockerId;
        this.lockerPassword = lockerPassword;
        this.isRead = true;
    }

    public NotificationEntity() {

    }

    public static NotificationEntity createNotification(UserEntity user, String lockerId, String lockerPassword) {
        return new NotificationEntity(user, lockerId, lockerPassword);
    }
}
