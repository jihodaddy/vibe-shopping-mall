package com.shop.domain.admin.dto;

import com.shop.domain.order.entity.Order;
import com.shop.domain.order.entity.OrderItem;
import com.shop.domain.order.entity.OrderItemStatus;
import com.shop.domain.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AdminOrderResponse {

    private Long id;
    private String orderNumber;
    private Long memberId;
    private String receiverName;
    private String receiverPhone;
    private OrderStatus status;
    private int finalPrice;
    private LocalDateTime createdAt;
    private List<AdminOrderItemResponse> items;

    public static AdminOrderResponse from(Order order) {
        return AdminOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .memberId(order.getMemberId())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .status(order.getStatus())
                .finalPrice(order.getFinalPrice())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(AdminOrderItemResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    @Builder
    public static class AdminOrderItemResponse {
        private Long productId;
        private String productName;
        private String optionInfo;
        private int qty;
        private int price;
        private OrderItemStatus status;

        public static AdminOrderItemResponse from(OrderItem item) {
            return AdminOrderItemResponse.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .optionInfo(item.getOptionInfo())
                    .qty(item.getQty())
                    .price(item.getPrice())
                    .status(item.getStatus())
                    .build();
        }
    }
}
