package com.shop.domain.member.service;

import com.shop.domain.member.dto.LoginRequest;
import com.shop.domain.member.dto.SignupRequest;
import com.shop.domain.member.dto.TokenResponse;
import com.shop.domain.member.entity.Member;
import com.shop.domain.member.repository.MemberRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import com.shop.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public void signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        Member member = Member.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .name(request.getName())
            .phone(request.getPhone())
            .build();
        memberRepository.save(member);
    }

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(member.getId(), "USER");
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        redisTemplate.opsForValue().set(
            "refresh:" + member.getId(),
            refreshToken,
            Duration.ofMillis(1209600000L)  // 14일
        );

        return TokenResponse.of(accessToken, refreshToken);
    }

    public void logout(String accessToken, Long memberId) {
        long expiration = jwtProvider.getExpiration(accessToken);
        if (expiration > 0) {
            redisTemplate.opsForValue().set(
                "blacklist:" + accessToken,
                "1",
                Duration.ofMillis(expiration)
            );
        }
        redisTemplate.delete("refresh:" + memberId);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.isValid(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        Long memberId = jwtProvider.getMemberId(refreshToken);
        Object stored = redisTemplate.opsForValue().get("refresh:" + memberId);
        if (stored == null || !stored.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        String newAccessToken = jwtProvider.createAccessToken(memberId, "USER");
        return TokenResponse.of(newAccessToken, null);
    }
}
