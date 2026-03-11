package com.shop.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverPhone;

    @NotBlank
    private String zipcode;

    @NotBlank
    private String address;

    private String addressDetail;
    private String deliveryMemo;

    private Long memberCouponId;
    private int usePoint = 0;

    @NotNull
    private String paymentMethod;  // TOSS, KAKAO_PAY, NAVER_PAY, CARD
}
