package com.example.shoppingmallproject.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;

    public CustomOAuth2User(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("nickname", userDTO.getNickname());
        attributes.put("email", userDTO.getEmail());
        attributes.put("profileImage", userDTO.getProfileImage());
        attributes.put("role", userDTO.getRole());

        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userDTO.getRole().toString();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return userDTO.getNickname();
    }

    public String getEmail() {
        return userDTO.getEmail();
    }
}
