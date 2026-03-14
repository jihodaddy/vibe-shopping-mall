package com.shop.domain.admin.dto;

import com.shop.domain.coupon.entity.CouponTarget;
import com.shop.domain.coupon.entity.CouponType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCouponCreateRequest {

    @NotBlank(message = "쿠폰 코드는 필수입니다.")
    private String code;

    @NotBlank(message = "쿠폰명은 필수입니다.")
    private String name;

    @NotNull(message = "쿠폰 타입은 필수입니다.")
    private CouponType type;

    @Min(value = 1, message = "할인 값은 1 이상이어야 합니다.")
    private int value;

    @Min(value = 0, message = "최소 주문 금액은 0 이상이어야 합니다.")
    private int minOrderPrice;

    private Integer maxDiscountPrice;

    private CouponTarget target;

    private Long targetId;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDateTime startAt;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDateTime endAt;

    private Integer totalQty;
}
