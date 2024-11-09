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

    //buyer
    @Column(name = "seller_account")
    private String sellerAccount;

    @Column(name = "seller_realname")
    private String sellerRealname;

    // common
    private String message;

    @Setter
    private boolean isRead;

    @ManyToOne
    @Column(name = "user_id")
    private UserEntity userId;

    // 판매자 전용 알림
    public NotificationEntity(NotificationLockerDTO notificationLockerDTO) {
        this.message = notificationLockerDTO.getMessage();
        this.lockerId = notificationLockerDTO.getLockerId();
        this.lockerPassword = notificationLockerDTO.getLockerPassword();
        this.isRead = notificationLockerDTO.isRead();
        this.userId = notificationLockerDTO.getUserId();
    }

    // 구매자 전용 알림
    public NotificationEntity(NotificationBuyerDTO NotificationBuyerDTO) {
        this.message = NotificationBuyerDTO.getMessage();
        this.sellerAccount = NotificationBuyerDTO.getSellerAccount();
        this.sellerRealname = NotificationBuyerDTO.getSellerRealname();
        this.isRead = NotificationBuyerDTO.isRead();
        this.userId = NotificationBuyerDTO.getUserId();
    }
}
