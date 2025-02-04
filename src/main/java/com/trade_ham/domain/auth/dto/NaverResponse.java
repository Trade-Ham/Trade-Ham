package com.trade_ham.domain.auth.dto;

import com.trade_ham.global.common.enums.Provider;

import java.util.Map;

public class NaverResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public Provider getProvider() {
        return Provider.NAVER;
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getNickName() {
        return attribute.get("name").toString();
    }

    @Override
    public String getProfileImage() {
        return attribute.get("profile_image").toString();
    }
}
