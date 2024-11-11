package com.trade_ham.global.common.exception;

public class NotificationNotCreateException extends RuntimeException{

    private final ErrorCode errorCode;

    public NotificationNotCreateException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
