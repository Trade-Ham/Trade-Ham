package com.example.shoppingmallproject.login.dto;


import com.example.shoppingmallproject.login.domain.ProviderType;

import java.util.Map;

public class OAuth2Response {

    private final Map<String, Object> attribute;
    private final String role;

    public OAuth2Response(Map<String, Object> attribute, String role) {
        this.attribute = (Map<String, Object>) attribute.get("kakao_account");
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public ProviderType getProvider() {
        return ProviderType.KAKAO;
    }

    public String getEmail() {
        return attribute.get("email").toString();
    }

    public String getNickName() {
        Map<String, Object> profile = (Map<String, Object>) attribute.get("profile");
        return profile.get("nickname").toString();
    }

    public String getProfileImage() {
        Map<String, Object> profile = (Map<String, Object>) attribute.get("profile");
        return profile.get("profile_image_url").toString();
    }

}
