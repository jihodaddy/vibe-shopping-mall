package com.shop.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long productId;

    private Long optionId;

    @Column(nullable = false, length = 200)
    private String productName;

    @Column(length = 200)
    private String optionInfo;

    @Column(nullable = false)
    private int qty;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderItemStatus status = OrderItemStatus.PENDING;

    @Builder
    public OrderItem(Order order, Long productId, Long optionId,
                     String productName, String optionInfo, int qty, int price) {
        this.order = order;
        this.productId = productId;
        this.optionId = optionId;
        this.productName = productName;
        this.optionInfo = optionInfo;
        this.qty = qty;
        this.price = price;
    }

    public void updateStatus(OrderItemStatus status) {
        this.status = status;
    }
}
