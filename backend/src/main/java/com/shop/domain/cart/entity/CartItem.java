package com.shop.domain.cart.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(nullable = false)
    private Long productId;

    private Long optionId;

    @Column(nullable = false)
    private int qty = 1;

    @Column(nullable = false)
    private LocalDateTime addedAt = LocalDateTime.now();

    @Builder
    public CartItem(Cart cart, Long productId, Long optionId, int qty) {
        this.cart = cart;
        this.productId = productId;
        this.optionId = optionId;
        this.qty = qty;
    }

    public void updateQty(int qty) {
        this.qty = qty;
    }

    public void addQty(int qty) {
        this.qty += qty;
    }
}
