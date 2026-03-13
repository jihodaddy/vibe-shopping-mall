package com.shop.domain.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCouponIssueRequest {

    @NotNull(message = "쿠폰 ID는 필수입니다.")
    private Long couponId;

    /**
     * null or empty = bulk issue to all active members
     * non-empty = issue to specific members
     */
    private List<Long> memberIds;
}
