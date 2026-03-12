package com.shop.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCategoryUpdateRequest {

    private String name;
    private int sortOrder;
    private boolean isActive;
}
