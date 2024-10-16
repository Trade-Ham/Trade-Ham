package com.example.shoppingmallproject.login.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UpdateUserInfoRequest {
    private String realname;
    private String account;
}
