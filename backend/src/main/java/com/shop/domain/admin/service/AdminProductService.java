package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.inventory.entity.InventoryType;
import com.shop.domain.inventory.service.InventoryService;
import com.shop.domain.product.entity.*;
import com.shop.domain.product.repository.CategoryRepository;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryService inventoryService;

    public Long createProduct(AdminProductCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
            .category(category)
            .name(request.getName())
            .price(request.getPrice())
            .discountRate(request.getDiscountRate())
            .stockQty(request.getStockQty())
            .description(request.getDescription())
            .build();
        productRepository.save(product);

        if (request.getImageUrls() != null) {
            final int[] sortOrder = {0};
            request.getImageUrls().forEach((url, isMain) ->
                product.addImage(ProductImage.builder()
                    .product(product)
                    .url(url)
                    .isMain(isMain)
                    .sortOrder(sortOrder[0]++)
                    .build()));
        }

        return product.getId();
    }

    public void updateProduct(Long id, AdminProductUpdateRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.update(
            request.getName(),
            request.getPrice(),
            request.getDescription(),
            request.getDiscountRate(),
            request.getStockQty(),
            request.getStatus()
        );
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.changeStatus(ProductStatus.DELETED);
    }

    public void bulkUpdateStatus(List<Long> ids, ProductStatus status) {
        productRepository.bulkUpdateStatus(ids, status);
    }

    @Transactional(readOnly = true)
    public Page<AdminProductResponse> getProductList(Pageable pageable, String keyword,
                                                      Long categoryId, ProductStatus status) {
        return productRepository.findByCondition(keyword, categoryId, status, pageable)
            .map(AdminProductResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminProductResponse getProductDetail(Long id) {
        Product product = productRepository.findByIdWithImages(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        product.getOptions().size(); // trigger lazy load within transaction

        return AdminProductResponse.from(product);
    }

    public void adjustStock(Long productId, AdminStockAdjustRequest request) {
        int delta = request.getDelta();
        if (delta > 0) {
            inventoryService.increase(productId, request.getOptionId(), delta,
                InventoryType.ADMIN_ADJUST, null);
        } else if (delta < 0) {
            inventoryService.decrease(productId, request.getOptionId(), -delta,
                InventoryType.ADMIN_ADJUST, null);
        }
    }

    // Category management
    @Transactional(readOnly = true)
    public List<AdminCategoryResponse> getCategoryTree() {
        return categoryRepository.findRootCategories().stream()
            .map(AdminCategoryResponse::from)
            .collect(Collectors.toList());
    }

    public Long createCategory(AdminCategoryCreateRequest request) {
        Category parent = null;
        byte depth = 1;

        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
            depth = (byte) (parent.getDepth() + 1);
            if (depth > 3) {
                throw new BusinessException(ErrorCode.CATEGORY_DEPTH_EXCEEDED);
            }
        }

        Category category = Category.builder()
            .parent(parent)
            .name(request.getName())
            .depth(depth)
            .build();

        categoryRepository.save(category);
        return category.getId();
    }

    public void updateCategory(Long id, AdminCategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        category.update(request.getName(), request.getSortOrder(), request.isActive());
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        category.deactivate();
    }
}
