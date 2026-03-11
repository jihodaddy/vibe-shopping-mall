package com.shop.domain.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSearchCondition {
    private Long categoryId;
    private String keyword;
    private String sort = "LATEST";  // LATEST, PRICE_ASC, PRICE_DESC, POPULAR
    private int page = 0;
    private int size = 20;
    private Boolean isBest;
    private Boolean isNew;
}
