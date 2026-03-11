package com.shop.domain.product.entity;

import com.shop.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 200)
    private String name;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int discountRate = 0;

    @Column(nullable = false)
    private int stockQty = 0;

    @Version
    private Long version;  // Optimistic Lock

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ON_SALE;

    @Column(nullable = false)
    private boolean isBest = false;

    @Column(nullable = false)
    private boolean isNew = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    @Builder
    public Product(Category category, String name, String description,
                   int price, int discountRate, int stockQty) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discountRate = discountRate;
        this.stockQty = stockQty;
    }

    public int getDiscountPrice() {
        return price - (price * discountRate / 100);
    }

    public void decreaseStock(int qty) {
        this.stockQty -= qty;
        if (this.stockQty <= 0) {
            this.stockQty = 0;
            this.status = ProductStatus.SOLD_OUT;
        }
    }

    public void increaseStock(int qty) {
        this.stockQty += qty;
        if (this.status == ProductStatus.SOLD_OUT && this.stockQty > 0) {
            this.status = ProductStatus.ON_SALE;
        }
    }

    public void addImage(ProductImage image) {
        this.images.add(image);
    }
}
