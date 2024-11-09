package com.trade_ham.domain.notifiaction.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LockerNotificationDTO {
    private final String lockerId;
    private final String lockerPassword;
    private final boolean isRead;

}
