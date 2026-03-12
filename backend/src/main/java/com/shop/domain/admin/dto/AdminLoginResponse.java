package com.shop.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminLoginResponse {

    private final String accessToken;
    private final String name;
    private final String role;

    public static AdminLoginResponse of(String accessToken, String name, String role) {
        return new AdminLoginResponse(accessToken, name, role);
    }
}
