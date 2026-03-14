package com.shop.domain.admin.controller;

import com.shop.domain.admin.dto.*;
import com.shop.domain.coupon.entity.CouponType;
import com.shop.domain.admin.service.AdminCouponService;
import com.shop.global.response.ApiResponse;
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
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    @PostMapping("/api/admin/coupons")
    public ResponseEntity<ApiResponse<Long>> createCoupon(
            @Valid @RequestBody AdminCouponCreateRequest request) {
        Long id = adminCouponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(id));
    }

    @GetMapping("/api/admin/coupons")
    public ResponseEntity<ApiResponse<Page<AdminCouponResponse>>> getCouponList(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CouponType type,
            @RequestParam(required = false) Boolean isActive) {
        Page<AdminCouponResponse> page = adminCouponService.getCouponList(pageable, keyword, type, isActive);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/api/admin/coupons/{id}")
    public ResponseEntity<ApiResponse<AdminCouponResponse>> getCouponDetail(
            @PathVariable Long id) {
        AdminCouponResponse response = adminCouponService.getCouponDetail(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/api/admin/coupons/{id}")
    public ResponseEntity<ApiResponse<Void>> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody AdminCouponUpdateRequest request) {
        adminCouponService.updateCoupon(id, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/api/admin/coupons/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateCoupon(@PathVariable Long id) {
        adminCouponService.deactivateCoupon(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/api/admin/coupons/issue")
    public ResponseEntity<ApiResponse<Integer>> issueCoupon(
            @Valid @RequestBody AdminCouponIssueRequest request) {
        int issuedCount = adminCouponService.issueCoupon(request);
        return ResponseEntity.ok(ApiResponse.ok(issuedCount));
    }
}
