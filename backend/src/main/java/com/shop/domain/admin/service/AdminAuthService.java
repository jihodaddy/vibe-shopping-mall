package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.AdminLoginRequest;
import com.shop.domain.admin.dto.AdminLoginResponse;
import com.shop.domain.admin.entity.AdminUser;
import com.shop.domain.admin.repository.AdminUserRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import com.shop.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    public AdminLoginResponse login(AdminLoginRequest request) {
        AdminUser admin = adminUserRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        if (!admin.isActive()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        String roleString = admin.getRole().name();
        String accessToken = jwtProvider.createAccessToken(admin.getId(), roleString);

        redisTemplate.opsForValue().set(
            "admin:session:" + admin.getId(),
            admin.getEmail(),
            Duration.ofHours(8)
        );

        return AdminLoginResponse.of(accessToken, admin.getName(), roleString);
    }

    public void logout(String accessToken, Long adminId) {
        long expiration = jwtProvider.getExpiration(accessToken);
        if (expiration > 0) {
            redisTemplate.opsForValue().set(
                "blacklist:" + accessToken,
                "1",
                Duration.ofMillis(expiration)
            );
        }
        redisTemplate.delete("admin:session:" + adminId);
    }
}
