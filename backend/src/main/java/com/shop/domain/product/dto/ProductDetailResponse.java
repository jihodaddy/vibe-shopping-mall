package com.shop.domain.product.dto;

import com.shop.domain.product.entity.Product;
import com.shop.domain.product.entity.ProductImage;
import com.shop.domain.product.entity.ProductOption;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductDetailResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final int price;
    private final int discountRate;
    private final int discountPrice;
    private final int stockQty;
    private final String status;
    private final boolean isBest;
    private final boolean isNew;
    private final String categoryName;
    private final List<ImageDto> images;
    private final List<OptionDto> options;

    public ProductDetailResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.discountRate = product.getDiscountRate();
        this.discountPrice = product.getDiscountPrice();
        this.stockQty = product.getStockQty();
        this.status = product.getStatus().name();
        this.isBest = product.isBest();
        this.isNew = product.isNew();
        this.categoryName = product.getCategory().getName();
        this.images = product.getImages().stream().map(ImageDto::new).toList();
        this.options = product.getOptions().stream().map(OptionDto::new).toList();
    }

    @Getter
    public static class ImageDto {
        private final Long id;
        private final String url;
        private final boolean isMain;
        private final int sortOrder;

        public ImageDto(ProductImage img) {
            this.id = img.getId();
            this.url = img.getUrl();
            this.isMain = img.isMain();
            this.sortOrder = img.getSortOrder();
        }
    }

    @Getter
    public static class OptionDto {
        private final Long id;
        private final String optionName;
        private final String optionValue;
        private final int addPrice;
        private final int stockQty;

        public OptionDto(ProductOption opt) {
            this.id = opt.getId();
            this.optionName = opt.getOptionName();
            this.optionValue = opt.getOptionValue();
            this.addPrice = opt.getAddPrice();
            this.stockQty = opt.getStockQty();
        }
    }
}
