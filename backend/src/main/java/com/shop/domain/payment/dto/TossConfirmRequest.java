package com.shop.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossConfirmRequest {

    @NotBlank
    private String paymentKey;

    @NotBlank
    private String orderId;   // idempotency_key 역할

    @NotNull
    private Integer amount;
}
