package com.shop.domain.product.controller;

import com.shop.domain.product.dto.ProductDetailResponse;
import com.shop.domain.product.dto.ProductListResponse;
import com.shop.domain.product.dto.ProductSearchCondition;
import com.shop.domain.product.service.ProductService;
import com.shop.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductListResponse>>> getProducts(
            @ModelAttribute ProductSearchCondition condition) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProducts(condition)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProduct(id)));
    }
}
