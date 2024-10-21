package com.example.shoppingmallproject.dto;

import com.example.shoppingmallproject.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String nickname;
    private String email;
    private String profileImage;

    private Role role;
}
