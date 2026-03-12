package com.shop.domain.admin.dto;

import com.shop.domain.order.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AdminOrderSearchCondition {

    private String keyword;
    private OrderStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}
