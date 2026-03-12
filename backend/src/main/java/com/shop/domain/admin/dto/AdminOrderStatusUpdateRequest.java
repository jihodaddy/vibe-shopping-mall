package com.shop.domain.admin.dto;

import com.shop.domain.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminOrderStatusUpdateRequest {

    @NotNull
    private OrderStatus status;
}
