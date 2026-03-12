package com.shop.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_shipping")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderShipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Column(length = 50)
    private String courier;

    @Column(length = 100)
    private String trackingNumber;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    @Builder
    public OrderShipping(Long orderId, String courier, String trackingNumber) {
        this.orderId = orderId;
        this.courier = courier;
        this.trackingNumber = trackingNumber;
    }

    public void update(String courier, String trackingNumber) {
        this.courier = courier;
        this.trackingNumber = trackingNumber;
        if (this.shippedAt == null) {
            this.shippedAt = LocalDateTime.now();
        }
    }

    public void markDelivered() {
        this.deliveredAt = LocalDateTime.now();
    }
}
