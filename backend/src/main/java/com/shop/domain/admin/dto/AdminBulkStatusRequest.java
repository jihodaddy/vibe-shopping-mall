package com.shop.domain.admin.dto;

import com.shop.domain.product.entity.ProductStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminBulkStatusRequest {

    @NotEmpty(message = "상품 ID 목록은 비어있을 수 없습니다.")
    private List<Long> ids;

    @NotNull(message = "변경할 상태는 필수입니다.")
    private ProductStatus status;
}
