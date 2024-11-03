package com.trade_ham.domain.auth.dto;

import com.trade_ham.global.common.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String nickname;
    private String email;
    private String profileImage;

    private Role role;
}
