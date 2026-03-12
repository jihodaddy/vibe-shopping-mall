package com.shop.domain.admin.controller;

import com.shop.domain.admin.dto.*;
import com.shop.domain.admin.service.AdminProductService;
import com.shop.global.dto.PresignedUrlRequest;
import com.shop.global.dto.PresignedUrlResponse;
import com.shop.global.response.ApiResponse;
import com.shop.global.service.S3FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final S3FileService s3FileService;

    // ===== Product CRUD =====

    @PostMapping("/api/admin/products")
    public ResponseEntity<ApiResponse<Long>> createProduct(
            @Valid @RequestBody AdminProductCreateRequest request) {
        Long id = adminProductService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(id));
    }

    @GetMapping("/api/admin/products")
    public ResponseEntity<ApiResponse<Page<AdminProductResponse>>> getProductList(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        Page<AdminProductResponse> page = adminProductService.getProductList(pageable, keyword, categoryId, status);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/api/admin/products/{id}")
    public ResponseEntity<ApiResponse<AdminProductResponse>> getProductDetail(
            @PathVariable Long id) {
        AdminProductResponse response = adminProductService.getProductDetail(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/api/admin/products/{id}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody AdminProductUpdateRequest request) {
        adminProductService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/api/admin/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/api/admin/products/status")
    public ResponseEntity<ApiResponse<Void>> bulkUpdateStatus(
            @Valid @RequestBody AdminBulkStatusRequest request) {
        adminProductService.bulkUpdateStatus(request.getIds(), request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/api/admin/products/{id}/stock")
    public ResponseEntity<ApiResponse<Void>> adjustStock(
            @PathVariable Long id,
            @Valid @RequestBody AdminStockAdjustRequest request) {
        adminProductService.adjustStock(id, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ===== File Upload =====

    @PostMapping("/api/admin/files/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request) {
        PresignedUrlResponse response = s3FileService.generatePresignedUrl(
            request.getFileName(), request.getContentType());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ===== Category Management =====

    @GetMapping("/api/admin/categories")
    public ResponseEntity<ApiResponse<List<AdminCategoryResponse>>> getCategoryTree() {
        List<AdminCategoryResponse> categories = adminProductService.getCategoryTree();
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }

    @PostMapping("/api/admin/categories")
    public ResponseEntity<ApiResponse<Long>> createCategory(
            @Valid @RequestBody AdminCategoryCreateRequest request) {
        Long id = adminProductService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(id));
    }

    @PutMapping("/api/admin/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> updateCategory(
            @PathVariable Long id,
            @RequestBody AdminCategoryUpdateRequest request) {
        adminProductService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/api/admin/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        adminProductService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
