package com.shop.domain.member.controller;

import com.shop.domain.member.dto.LoginRequest;
import com.shop.domain.member.dto.SignupRequest;
import com.shop.domain.member.dto.TokenResponse;
import com.shop.domain.member.service.AuthService;
import com.shop.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        TokenResponse tokens = authService.login(request);

        // Refresh Token → HttpOnly Cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
            .httpOnly(true)
            .secure(false)  // 로컬 개발용 (prod에서는 true)
            .sameSite("Strict")
            .maxAge(Duration.ofDays(14))
            .path("/api/v1/auth/refresh")
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(ApiResponse.ok(TokenResponse.of(tokens.getAccessToken(), null)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal Long memberId) {

        String token = authHeader.substring(7);
        authService.logout(token, memberId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Refresh token이 없습니다.", "INVALID_TOKEN"));
        }
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(refreshToken)));
    }
}
