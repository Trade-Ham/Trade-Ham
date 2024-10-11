package com.example.shoppingmallproject.login.dto;

import com.example.shoppingmallproject.login.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private User userInfo;
}
