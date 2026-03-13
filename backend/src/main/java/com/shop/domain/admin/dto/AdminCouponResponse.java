package com.shop.domain.admin.dto;

import com.shop.domain.admin.entity.Coupon;
import com.shop.domain.admin.entity.CouponTarget;
import com.shop.domain.admin.entity.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCouponResponse {

    private Long id;
    private String code;
    private String name;
    private CouponType type;
    private int value;
    private int minOrderPrice;
    private Integer maxDiscountPrice;
    private CouponTarget target;
    private Long targetId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer totalQty;
    private int usedQty;
    private boolean active;
    private long issuedCount;

    public static AdminCouponResponse from(Coupon coupon) {
        return AdminCouponResponse.builder()
            .id(coupon.getId())
            .code(coupon.getCode())
            .name(coupon.getName())
            .type(coupon.getType())
            .value(coupon.getValue())
            .minOrderPrice(coupon.getMinOrderPrice())
            .maxDiscountPrice(coupon.getMaxDiscountPrice())
            .target(coupon.getTarget())
            .targetId(coupon.getTargetId())
            .startAt(coupon.getStartAt())
            .endAt(coupon.getEndAt())
            .totalQty(coupon.getTotalQty())
            .usedQty(coupon.getUsedQty())
            .active(coupon.isActive())
            .build();
    }

    public static AdminCouponResponse from(Coupon coupon, long issuedCount) {
        AdminCouponResponse response = from(coupon);
        response.issuedCount = issuedCount;
        return response;
    }
}
