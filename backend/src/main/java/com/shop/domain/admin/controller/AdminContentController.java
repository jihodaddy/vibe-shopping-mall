package com.shop.domain.admin.controller;

import com.shop.domain.admin.dto.*;
import com.shop.domain.content.entity.BannerPosition;
import com.shop.domain.admin.service.AdminContentService;
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

@RestController
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService adminContentService;
    private final S3FileService s3FileService;

    @PostMapping("/api/admin/banners")
    public ResponseEntity<ApiResponse<Long>> createBanner(
            @Valid @RequestBody AdminBannerCreateRequest request) {
        Long id = adminContentService.createBanner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(id));
    }

    @GetMapping("/api/admin/banners")
    public ResponseEntity<ApiResponse<Page<AdminBannerResponse>>> getBannerList(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) BannerPosition position,
            @RequestParam(required = false) Boolean isActive) {
        Page<AdminBannerResponse> page = adminContentService.getBannerList(pageable, position, isActive);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/api/admin/banners/{id}")
    public ResponseEntity<ApiResponse<AdminBannerResponse>> getBannerDetail(
            @PathVariable Long id) {
        AdminBannerResponse response = adminContentService.getBannerDetail(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/api/admin/banners/{id}")
    public ResponseEntity<ApiResponse<Void>> updateBanner(
            @PathVariable Long id,
            @Valid @RequestBody AdminBannerUpdateRequest request) {
        adminContentService.updateBanner(id, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/api/admin/banners/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        adminContentService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PutMapping("/api/admin/banners/sort")
    public ResponseEntity<ApiResponse<Void>> updateBannerSort(
            @Valid @RequestBody AdminBannerSortRequest request) {
        adminContentService.updateBannerSort(request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/api/admin/banners/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getBannerPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request) {
        PresignedUrlResponse response = s3FileService.generatePresignedUrl(
            request.getFileName(), request.getContentType());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
