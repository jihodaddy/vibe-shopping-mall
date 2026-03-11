package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.AdminLoginRequest;
import com.shop.domain.admin.dto.AdminLoginResponse;
import com.shop.domain.admin.entity.AdminRole;
import com.shop.domain.admin.entity.AdminStatus;
import com.shop.domain.admin.entity.AdminUser;
import com.shop.domain.admin.repository.AdminUserRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import com.shop.global.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private AdminAuthService adminAuthService;

    private AdminUser activeAdmin;
    private AdminUser inactiveAdmin;

    @BeforeEach
    void setUp() {
        activeAdmin = AdminUser.builder()
            .email("admin@shop.com")
            .password("encodedPassword")
            .name("관리자")
            .role(AdminRole.ADMIN)
            .status(AdminStatus.ACTIVE)
            .build();

        inactiveAdmin = AdminUser.builder()
            .email("inactive@shop.com")
            .password("encodedPassword")
            .name("비활성관리자")
            .role(AdminRole.ADMIN)
            .status(AdminStatus.INACTIVE)
            .build();
    }

    @Test
    void 어드민_로그인_성공() {
        // given
        AdminLoginRequest request = new AdminLoginRequest("admin@shop.com", "rawPassword");
        given(adminUserRepository.findByEmail("admin@shop.com")).willReturn(Optional.of(activeAdmin));
        given(passwordEncoder.matches("rawPassword", "encodedPassword")).willReturn(true);
        given(jwtProvider.createAccessToken(any(), eq("ADMIN"))).willReturn("admin-access-token");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        AdminLoginResponse response = adminAuthService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("admin-access-token");
        assertThat(response.getName()).isEqualTo("관리자");
        assertThat(response.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void 비활성_어드민_로그인_실패() {
        // given
        AdminLoginRequest request = new AdminLoginRequest("inactive@shop.com", "rawPassword");
        given(adminUserRepository.findByEmail("inactive@shop.com")).willReturn(Optional.of(inactiveAdmin));
        given(passwordEncoder.matches("rawPassword", "encodedPassword")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> adminAuthService.login(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
    }

    @Test
    void 어드민_이메일_없는_경우_로그인_실패() {
        // given
        AdminLoginRequest request = new AdminLoginRequest("notfound@shop.com", "rawPassword");
        given(adminUserRepository.findByEmail("notfound@shop.com")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminAuthService.login(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    void 어드민_비밀번호_불일치_로그인_실패() {
        // given
        AdminLoginRequest request = new AdminLoginRequest("admin@shop.com", "wrongPassword");
        given(adminUserRepository.findByEmail("admin@shop.com")).willReturn(Optional.of(activeAdmin));
        given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> adminAuthService.login(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);
    }

    @Test
    void ADMIN_역할_토큰_생성_확인() {
        // given
        AdminLoginRequest request = new AdminLoginRequest("admin@shop.com", "rawPassword");
        given(adminUserRepository.findByEmail("admin@shop.com")).willReturn(Optional.of(activeAdmin));
        given(passwordEncoder.matches("rawPassword", "encodedPassword")).willReturn(true);
        given(jwtProvider.createAccessToken(any(), eq("ADMIN"))).willReturn("admin-access-token");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        adminAuthService.login(request);

        // then - 토큰 클레임에 role=ADMIN 포함 확인
        verify(jwtProvider).createAccessToken(any(), eq("ADMIN"));
    }

    @Test
    void SUPER_ADMIN_역할_토큰_생성_확인() {
        // given
        AdminUser superAdmin = AdminUser.builder()
            .email("super@shop.com")
            .password("encodedPassword")
            .name("슈퍼관리자")
            .role(AdminRole.SUPER_ADMIN)
            .status(AdminStatus.ACTIVE)
            .build();

        AdminLoginRequest request = new AdminLoginRequest("super@shop.com", "rawPassword");
        given(adminUserRepository.findByEmail("super@shop.com")).willReturn(Optional.of(superAdmin));
        given(passwordEncoder.matches("rawPassword", "encodedPassword")).willReturn(true);
        given(jwtProvider.createAccessToken(any(), eq("SUPER_ADMIN"))).willReturn("super-access-token");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        AdminLoginResponse response = adminAuthService.login(request);

        // then
        assertThat(response.getRole()).isEqualTo("SUPER_ADMIN");
        verify(jwtProvider).createAccessToken(any(), eq("SUPER_ADMIN"));
    }
}
