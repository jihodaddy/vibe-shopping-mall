package com.shop.domain.product.service;

import com.shop.domain.product.dto.ProductDetailResponse;
import com.shop.domain.product.dto.ProductListResponse;
import com.shop.domain.product.dto.ProductSearchCondition;
import com.shop.domain.product.entity.Product;
import com.shop.domain.product.entity.ProductStatus;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductListResponse> getProducts(ProductSearchCondition condition) {
        Sort sort = switch (condition.getSort()) {
            case "PRICE_ASC"  -> Sort.by("price").ascending();
            case "PRICE_DESC" -> Sort.by("price").descending();
            default           -> Sort.by("createdAt").descending();
        };
        Pageable pageable = PageRequest.of(condition.getPage(), condition.getSize(), sort);

        // 간단한 구현 - QueryDSL 확장 가능
        return productRepository.findAll(pageable)
            .map(ProductListResponse::new);
    }

    public ProductDetailResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return new ProductDetailResponse(product);
    }
}
