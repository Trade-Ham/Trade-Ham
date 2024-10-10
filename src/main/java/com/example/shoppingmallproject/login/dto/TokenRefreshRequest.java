package com.example.shoppingmallproject.login.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
