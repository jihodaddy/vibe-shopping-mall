package com.shop.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long optionId;
    private String optionInfo;
    private int price;
    private int qty;
    private String thumbnailUrl;
}
