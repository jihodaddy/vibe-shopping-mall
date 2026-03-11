package com.shop.domain.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    private Long optionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryType type;

    @Column(nullable = false)
    private int delta;

    @Column(nullable = false)
    private int balanceAfter;

    private Long orderId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public InventoryHistory(Long productId, Long optionId, InventoryType type,
                             int delta, int balanceAfter, Long orderId) {
        this.productId = productId;
        this.optionId = optionId;
        this.type = type;
        this.delta = delta;
        this.balanceAfter = balanceAfter;
        this.orderId = orderId;
    }
}
