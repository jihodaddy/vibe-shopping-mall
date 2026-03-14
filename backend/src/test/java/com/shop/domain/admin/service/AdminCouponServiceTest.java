package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.coupon.entity.*;
import com.shop.domain.coupon.repository.CouponRepository;
import com.shop.domain.coupon.repository.MemberCouponRepository;
import com.shop.domain.member.entity.Member;
import com.shop.domain.member.entity.MemberStatus;
import com.shop.domain.member.repository.MemberRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private MemberCouponRepository memberCouponRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AdminCouponService adminCouponService;

    private Coupon testCoupon;
    private Member testMember;

    @BeforeEach
    void setUp() {
        testCoupon = Coupon.builder()
            .code("TEST2024")
            .name("테스트 쿠폰")
            .type(CouponType.FIXED)
            .value(5000)
            .minOrderPrice(30000)
            .maxDiscountPrice(null)
            .target(CouponTarget.ALL)
            .startAt(LocalDateTime.now().minusDays(1))
            .endAt(LocalDateTime.now().plusDays(30))
            .totalQty(100)
            .build();

        testMember = Member.builder()
            .email("test@test.com")
            .name("테스트 회원")
            .password("password")
            .phone("01012345678")
            .build();
    }

    @Test
    void 쿠폰_생성_성공() {
        // given
        AdminCouponCreateRequest request = new AdminCouponCreateRequest(
            "NEW2024", "신규 쿠폰", CouponType.FIXED, 3000, 20000, null,
            CouponTarget.ALL, null, LocalDateTime.now(), LocalDateTime.now().plusDays(30), 50
        );

        given(couponRepository.existsByCode("NEW2024")).willReturn(false);
        given(couponRepository.save(any(Coupon.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adminCouponService.createCoupon(request);

        // then
        verify(couponRepository).existsByCode("NEW2024");
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void 쿠폰_생성_중복코드_실패() {
        // given
        AdminCouponCreateRequest request = new AdminCouponCreateRequest(
            "TEST2024", "중복 쿠폰", CouponType.FIXED, 3000, 20000, null,
            CouponTarget.ALL, null, LocalDateTime.now(), LocalDateTime.now().plusDays(30), 50
        );

        given(couponRepository.existsByCode("TEST2024")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> adminCouponService.createCoupon(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_COUPON_CODE);

        verify(couponRepository, never()).save(any());
    }

    @Test
    void 쿠폰_수정_성공() {
        // given
        AdminCouponUpdateRequest request = new AdminCouponUpdateRequest(
            "수정된 쿠폰", CouponType.FIXED, 10000, 10000, 5000,
            CouponTarget.ALL, null, LocalDateTime.now(), LocalDateTime.now().plusDays(60), 200
        );

        given(couponRepository.findById(1L)).willReturn(Optional.of(testCoupon));

        // when
        adminCouponService.updateCoupon(1L, request);

        // then
        verify(couponRepository).findById(1L);
        assertThat(testCoupon.getName()).isEqualTo("수정된 쿠폰");
        assertThat(testCoupon.getType()).isEqualTo(CouponType.FIXED);
        assertThat(testCoupon.getValue()).isEqualTo(10000);
    }

    @Test
    void 쿠폰_수정_없는_쿠폰_실패() {
        // given
        AdminCouponUpdateRequest request = new AdminCouponUpdateRequest(
            "수정", CouponType.FIXED, 5000, 0, null,
            CouponTarget.ALL, null, LocalDateTime.now(), LocalDateTime.now().plusDays(30), null
        );

        given(couponRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCouponService.updateCoupon(999L, request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_NOT_FOUND);
    }

    @Test
    void 쿠폰_비활성화_성공() {
        // given
        given(couponRepository.findById(1L)).willReturn(Optional.of(testCoupon));

        // when
        adminCouponService.deactivateCoupon(1L);

        // then
        assertThat(testCoupon.isActive()).isFalse();
    }

    @Test
    void 쿠폰_비활성화_없는_쿠폰_실패() {
        // given
        given(couponRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCouponService.deactivateCoupon(999L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_NOT_FOUND);
    }

    @Test
    void 쿠폰_목록_조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Coupon> couponPage = new PageImpl<>(List.of(testCoupon));
        given(couponRepository.findByCondition(null, null, null, pageable))
            .willReturn(couponPage);
        List<Object[]> countResults = new java.util.ArrayList<>();
        countResults.add(new Object[]{null, 5L});
        given(memberCouponRepository.countByCouponIdIn(anyList()))
            .willReturn(countResults);

        // when
        Page<AdminCouponResponse> result = adminCouponService.getCouponList(pageable, null, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void 쿠폰_상세_조회_성공() {
        // given
        given(couponRepository.findById(1L)).willReturn(Optional.of(testCoupon));
        given(memberCouponRepository.countByCouponId(any())).willReturn(10L);

        // when
        AdminCouponResponse response = adminCouponService.getCouponDetail(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("테스트 쿠폰");
        assertThat(response.getIssuedCount()).isEqualTo(10);
    }

    @Test
    void 쿠폰_상세_조회_없는_쿠폰_실패() {
        // given
        given(couponRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCouponService.getCouponDetail(999L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_NOT_FOUND);
    }

    @Test
    void 쿠폰_발급_특정_회원_성공() {
        // given
        AdminCouponIssueRequest request = new AdminCouponIssueRequest(1L, List.of(1L));

        given(couponRepository.findById(1L)).willReturn(Optional.of(testCoupon));
        given(memberRepository.findAllById(List.of(1L))).willReturn(List.of(testMember));
        given(memberCouponRepository.existsByMemberIdAndCouponId(any(), any())).willReturn(false);

        // when
        int count = adminCouponService.issueCoupon(request);

        // then
        assertThat(count).isEqualTo(1);
        verify(memberCouponRepository).saveAll(anyList());
    }

    @Test
    void 쿠폰_발급_이미_발급된_회원_스킵() {
        // given
        AdminCouponIssueRequest request = new AdminCouponIssueRequest(1L, List.of(1L));

        given(couponRepository.findById(1L)).willReturn(Optional.of(testCoupon));
        given(memberRepository.findAllById(List.of(1L))).willReturn(List.of(testMember));
        given(memberCouponRepository.existsByMemberIdAndCouponId(any(), any())).willReturn(true);

        // when
        int count = adminCouponService.issueCoupon(request);

        // then
        assertThat(count).isEqualTo(0);
        verify(memberCouponRepository, never()).saveAll(anyList());
    }

    @Test
    void 쿠폰_발급_비활성화_쿠폰_실패() {
        // given
        testCoupon.deactivate();
        AdminCouponIssueRequest request = new AdminCouponIssueRequest(1L, List.of(1L));

        given(couponRepository.findById(1L)).willReturn(Optional.of(testCoupon));

        // when & then
        assertThatThrownBy(() -> adminCouponService.issueCoupon(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_INACTIVE);
    }

    @Test
    void 쿠폰_전체발급_성공() {
        // given
        AdminCouponIssueRequest request = new AdminCouponIssueRequest(1L, null);

        given(couponRepository.findById(1L)).willReturn(Optional.of(testCoupon));
        given(memberRepository.findByStatus(MemberStatus.ACTIVE)).willReturn(List.of(testMember));
        given(memberCouponRepository.existsByMemberIdAndCouponId(any(), any())).willReturn(false);

        // when
        int count = adminCouponService.issueCoupon(request);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void 쿠폰_발급_수량_소진_시_중단() {
        // given
        Coupon exhaustedCoupon = Coupon.builder()
            .code("EXHAUST")
            .name("소진 쿠폰")
            .type(CouponType.FIXED)
            .value(1000)
            .minOrderPrice(0)
            .target(CouponTarget.ALL)
            .startAt(LocalDateTime.now().minusDays(1))
            .endAt(LocalDateTime.now().plusDays(30))
            .totalQty(1)
            .build();

        Member member1 = Member.builder()
            .email("m1@test.com").name("회원1").password("pw").phone("01011111111").build();
        Member member2 = Member.builder()
            .email("m2@test.com").name("회원2").password("pw").phone("01022222222").build();

        AdminCouponIssueRequest request = new AdminCouponIssueRequest(1L, List.of(1L, 2L));

        given(couponRepository.findById(1L)).willReturn(Optional.of(exhaustedCoupon));
        given(memberRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(member1, member2));
        given(memberCouponRepository.existsByMemberIdAndCouponId(any(), any())).willReturn(false);

        // when
        int count = adminCouponService.issueCoupon(request);

        // then - totalQty=1이므로 1명에게만 발급되어야 함
        assertThat(count).isEqualTo(1);
        assertThat(exhaustedCoupon.isExhausted()).isTrue();
    }

    @Test
    void 쿠폰_수정_발급후_타입변경_실패() {
        // given - usedQty를 1 이상으로 만들기 위해 incrementUsedQty 호출
        testCoupon.incrementUsedQty();

        AdminCouponUpdateRequest request = new AdminCouponUpdateRequest(
            "수정 쿠폰", CouponType.RATE, 10, 10000, 5000,
            CouponTarget.ALL, null, LocalDateTime.now(), LocalDateTime.now().plusDays(60), 200
        );

        given(couponRepository.findById(1L)).willReturn(Optional.of(testCoupon));

        // when & then - FIXED -> RATE 변경 시도, usedQty > 0이므로 실패해야 함
        assertThatThrownBy(() -> adminCouponService.updateCoupon(1L, request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_TYPE_CHANGE_NOT_ALLOWED);
    }

    @Test
    void 쿠폰_생성_종료일이_시작일_이전이면_실패() {
        // given
        AdminCouponCreateRequest request = new AdminCouponCreateRequest(
            "INVALID", "잘못된 쿠폰", CouponType.FIXED, 3000, 20000, null,
            CouponTarget.ALL, null, LocalDateTime.now().plusDays(30), LocalDateTime.now(), 50
        );

        // when & then
        assertThatThrownBy(() -> adminCouponService.createCoupon(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_DATE_RANGE);
    }
}
