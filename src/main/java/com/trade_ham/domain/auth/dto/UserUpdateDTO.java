package com.trade_ham.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String account; // 계좌번호
    private String realname;
}
