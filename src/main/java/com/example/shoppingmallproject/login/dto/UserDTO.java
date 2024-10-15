package com.example.shoppingmallproject.login.dto;

import com.example.shoppingmallproject.login.domain.ProviderType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDTO {

    private String nickname;
    private String email;
    private String role;
    private ProviderType provider;
}
