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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCouponService {

    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final MemberRepository memberRepository;

    public Long createCoupon(AdminCouponCreateRequest request) {
        validateDateRange(request.getStartAt(), request.getEndAt());

        if (couponRepository.existsByCode(request.getCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_COUPON_CODE);
        }

        Coupon coupon = Coupon.builder()
            .code(request.getCode())
            .name(request.getName())
            .type(request.getType())
            .value(request.getValue())
            .minOrderPrice(request.getMinOrderPrice())
            .maxDiscountPrice(request.getMaxDiscountPrice())
            .target(request.getTarget())
            .targetId(request.getTargetId())
            .startAt(request.getStartAt())
            .endAt(request.getEndAt())
            .totalQty(request.getTotalQty())
            .build();

        couponRepository.save(coupon);
        return coupon.getId();
    }

    public void updateCoupon(Long id, AdminCouponUpdateRequest request) {
        validateDateRange(request.getStartAt(), request.getEndAt());

        Coupon coupon = couponRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        if (coupon.getUsedQty() > 0 && coupon.getType() != request.getType()) {
            throw new BusinessException(ErrorCode.COUPON_TYPE_CHANGE_NOT_ALLOWED);
        }

        coupon.update(
            request.getName(),
            request.getType(),
            request.getValue(),
            request.getMinOrderPrice(),
            request.getMaxDiscountPrice(),
            request.getTarget(),
            request.getTargetId(),
            request.getStartAt(),
            request.getEndAt(),
            request.getTotalQty()
        );
    }

    public void deactivateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
        coupon.deactivate();
    }

    @Transactional(readOnly = true)
    public Page<AdminCouponResponse> getCouponList(Pageable pageable, String keyword,
                                                    CouponType type, Boolean isActive) {
        Page<Coupon> couponPage = couponRepository.findByCondition(keyword, type, isActive, pageable);

        List<Long> couponIds = couponPage.getContent().stream()
            .map(Coupon::getId)
            .toList();

        Map<Long, Long> issuedCountMap = getIssuedCountMap(couponIds);

        return couponPage.map(coupon -> {
            long issuedCount = issuedCountMap.getOrDefault(coupon.getId(), 0L);
            return AdminCouponResponse.from(coupon, issuedCount);
        });
    }

    @Transactional(readOnly = true)
    public AdminCouponResponse getCouponDetail(Long id) {
        Coupon coupon = couponRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
        long issuedCount = memberCouponRepository.countByCouponId(coupon.getId());
        return AdminCouponResponse.from(coupon, issuedCount);
    }

    public int issueCoupon(AdminCouponIssueRequest request) {
        Coupon coupon = couponRepository.findById(request.getCouponId())
            .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        if (!coupon.isActive()) {
            throw new BusinessException(ErrorCode.COUPON_INACTIVE);
        }
        if (coupon.isExpired()) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }

        List<Member> members;
        if (request.getMemberIds() == null || request.getMemberIds().isEmpty()) {
            members = memberRepository.findByStatus(MemberStatus.ACTIVE);
        } else {
            members = memberRepository.findAllById(request.getMemberIds());
        }

        List<MemberCoupon> toSave = new ArrayList<>();
        int issuedCount = 0;
        for (Member member : members) {
            if (coupon.isExhausted()) {
                break;
            }
            if (memberCouponRepository.existsByMemberIdAndCouponId(member.getId(), coupon.getId())) {
                continue;
            }

            MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .build();
            toSave.add(memberCoupon);
            coupon.incrementUsedQty();
            issuedCount++;
        }

        if (!toSave.isEmpty()) {
            memberCouponRepository.saveAll(toSave);
        }

        return issuedCount;
    }

    private Map<Long, Long> getIssuedCountMap(List<Long> couponIds) {
        if (couponIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Long> map = new HashMap<>();
        List<Object[]> results = memberCouponRepository.countByCouponIdIn(couponIds);
        for (Object[] row : results) {
            map.put((Long) row[0], (Long) row[1]);
        }
        return map;
    }

    private void validateDateRange(java.time.LocalDateTime startAt, java.time.LocalDateTime endAt) {
        if (startAt != null && endAt != null && !endAt.isAfter(startAt)) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
        }
    }
}
