package com.shop.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreateResponse {
    private final String orderNumber;
    private final String idempotencyKey;
    private final int finalPrice;

    public static OrderCreateResponse of(String orderNumber, String idempotencyKey, int finalPrice) {
        return new OrderCreateResponse(orderNumber, idempotencyKey, finalPrice);
    }
}
