package com.trade_ham.domain.locker.dto;

import com.trade_ham.domain.auth.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationLockerDTO {
    private String message;
    private Long lockerId;
    private String lockerPassword;
    private boolean isRead;
    private UserEntity userId;
}
