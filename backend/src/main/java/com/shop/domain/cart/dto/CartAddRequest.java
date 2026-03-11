package com.shop.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartAddRequest {

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    private Long optionId;

    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private int qty = 1;

    // 비회원 장바구니용 세션 키
    private String guestKey;
}
