package com.shop.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCategoryCreateRequest {

    private Long parentId;

    @NotBlank(message = "카테고리명은 필수입니다.")
    private String name;

    private int sortOrder;
}
