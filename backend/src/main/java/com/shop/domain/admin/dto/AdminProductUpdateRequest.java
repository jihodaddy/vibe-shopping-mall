package com.shop.domain.admin.dto;

import com.shop.domain.product.entity.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductUpdateRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    private String description;

    @Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
    private int discountRate;

    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private int stockQty;

    @NotNull(message = "상품 상태는 필수입니다.")
    private ProductStatus status;
}
