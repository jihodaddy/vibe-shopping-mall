package com.shop.domain.admin.dto;

import com.shop.domain.product.entity.Product;
import com.shop.domain.product.entity.ProductImage;
import com.shop.domain.product.entity.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AdminProductResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private int price;
    private int discountRate;
    private int discountPrice;
    private int stockQty;
    private ProductStatus status;
    private List<ImageInfo> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class ImageInfo {
        private Long id;
        private String url;
        private boolean isMain;
        private int sortOrder;

        public static ImageInfo from(ProductImage image) {
            return ImageInfo.builder()
                .id(image.getId())
                .url(image.getUrl())
                .isMain(image.isMain())
                .sortOrder(image.getSortOrder())
                .build();
        }
    }

    public static AdminProductResponse from(Product product) {
        return AdminProductResponse.builder()
            .id(product.getId())
            .categoryId(product.getCategory().getId())
            .categoryName(product.getCategory().getName())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .discountRate(product.getDiscountRate())
            .discountPrice(product.getDiscountPrice())
            .stockQty(product.getStockQty())
            .status(product.getStatus())
            .images(product.getImages().stream()
                .map(ImageInfo::from)
                .collect(Collectors.toList()))
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
    }
}
