package com.shop.domain.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminStockAdjustRequest {

    private Long optionId;

    @NotNull(message = "조정 수량은 필수입니다.")
    private Integer delta;
}
