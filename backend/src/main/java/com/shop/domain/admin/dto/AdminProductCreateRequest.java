package com.shop.domain.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductCreateRequest {

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    private String description;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    @Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
    private int discountRate;

    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
    private int stockQty;

    // key: url, value: isMain
    private Map<String, Boolean> imageUrls;
}
