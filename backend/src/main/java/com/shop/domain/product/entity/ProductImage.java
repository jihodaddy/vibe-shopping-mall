package com.shop.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private boolean isMain = false;

    @Column(nullable = false)
    private int sortOrder = 0;

    @Builder
    public ProductImage(Product product, String url, boolean isMain, int sortOrder) {
        this.product = product;
        this.url = url;
        this.isMain = isMain;
        this.sortOrder = sortOrder;
    }
}
