package com.trade_ham.global.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "잘못된 입력 값입니다."),

    // 403 Forbidden
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근이 거부되었습니다."),

    // 404 Not Found
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."),
    LOCKER_NOT_AVAILABLE(HttpStatus.NOT_FOUND, "LOCKER_NOT_AVAILABLE", "사용 가능한 사물함이 없습니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", "계좌번호을 찾을 수 없습니다."),
    REALNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "REALNAME_NOT_FOUND", "실제이름을 찾을 수 없습니다."),

    // 409 Conflict
    INVALID_PRODUCT_STATE(HttpStatus.CONFLICT, "INVALID_PRODUCT_STATE", "상품 상태가 올바르지 않습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}