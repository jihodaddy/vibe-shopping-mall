package com.shop.domain.admin.dto;

import com.shop.domain.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductUpdateRequest {

    private String name;
    private int price;
    private String description;
    private int discountRate;
    private int stockQty;
    private ProductStatus status;
}
