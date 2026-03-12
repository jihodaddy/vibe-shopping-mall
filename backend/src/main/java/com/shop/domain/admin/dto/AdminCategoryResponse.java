package com.shop.domain.admin.dto;

import com.shop.domain.product.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AdminCategoryResponse {

    private Long id;
    private Long parentId;
    private String name;
    private byte depth;
    private int sortOrder;
    private boolean isActive;
    private List<AdminCategoryResponse> children;

    public static AdminCategoryResponse from(Category category) {
        return AdminCategoryResponse.builder()
            .id(category.getId())
            .parentId(category.getParent() != null ? category.getParent().getId() : null)
            .name(category.getName())
            .depth(category.getDepth())
            .sortOrder(category.getSortOrder())
            .isActive(category.isActive())
            .children(category.getChildren().stream()
                .filter(Category::isActive)
                .map(AdminCategoryResponse::from)
                .collect(Collectors.toList()))
            .build();
    }
}
