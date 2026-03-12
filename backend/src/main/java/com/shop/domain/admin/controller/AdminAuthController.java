package com.shop.domain.admin.controller;

import com.shop.domain.admin.dto.AdminLoginRequest;
import com.shop.domain.admin.dto.AdminLoginResponse;
import com.shop.domain.admin.service.AdminAuthService;
import com.shop.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AdminLoginResponse>> login(
            @Valid @RequestBody AdminLoginRequest request) {

        AdminLoginResponse response = adminAuthService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @AuthenticationPrincipal Long adminId) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Authorization 헤더는 'Bearer '로 시작해야 합니다.", "INVALID_TOKEN"));
        }

        String token = authHeader.substring(7);
        adminAuthService.logout(token, adminId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
