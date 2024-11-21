package com.trade_ham.domain.locker.dto;

import com.trade_ham.domain.auth.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationBuyerDTO {
    private String message;
    private boolean isRead;
    private UserEntity userId;
}
