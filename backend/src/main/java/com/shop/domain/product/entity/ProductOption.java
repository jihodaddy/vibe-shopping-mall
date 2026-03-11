package com.shop.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 50)
    private String optionName;

    @Column(nullable = false, length = 100)
    private String optionValue;

    @Column(nullable = false)
    private int addPrice = 0;

    @Column(nullable = false)
    private int stockQty = 0;

    @Builder
    public ProductOption(Product product, String optionName, String optionValue,
                         int addPrice, int stockQty) {
        this.product = product;
        this.optionName = optionName;
        this.optionValue = optionValue;
        this.addPrice = addPrice;
        this.stockQty = stockQty;
    }

    public void decreaseStock(int qty) {
        this.stockQty -= qty;
    }

    public void increaseStock(int qty) {
        this.stockQty += qty;
    }
}
