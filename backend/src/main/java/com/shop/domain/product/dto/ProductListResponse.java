package com.shop.domain.product.dto;

import com.shop.domain.product.entity.Product;
import lombok.Getter;

@Getter
public class ProductListResponse {
    private final Long id;
    private final String name;
    private final int price;
    private final int discountRate;
    private final int discountPrice;
    private final String thumbnailUrl;
    private final boolean isBest;
    private final boolean isNew;
    private final String status;

    public ProductListResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.discountRate = product.getDiscountRate();
        this.discountPrice = product.getDiscountPrice();
        this.thumbnailUrl = product.getImages().stream()
            .filter(img -> img.isMain())
            .findFirst()
            .map(img -> img.getUrl())
            .orElse(null);
        this.isBest = product.isBest();
        this.isNew = product.isNew();
        this.status = product.getStatus().name();
    }
}
