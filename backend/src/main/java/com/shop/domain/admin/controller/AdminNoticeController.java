package com.shop.domain.admin.controller;

import com.shop.domain.admin.dto.*;
import com.shop.domain.admin.service.AdminNoticeService;
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
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;

    @PostMapping("/api/admin/cs/notices")
    public ResponseEntity<ApiResponse<Long>> createNotice(
            @Valid @RequestBody AdminNoticeCreateRequest request) {
        Long id = adminNoticeService.createNotice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(id));
    }

    @GetMapping("/api/admin/cs/notices")
    public ResponseEntity<ApiResponse<Page<AdminNoticeResponse>>> getNoticeList(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive) {
        Page<AdminNoticeResponse> page = adminNoticeService.getNoticeList(pageable, keyword, isActive);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/api/admin/cs/notices/{id}")
    public ResponseEntity<ApiResponse<AdminNoticeResponse>> getNoticeDetail(
            @PathVariable Long id) {
        AdminNoticeResponse response = adminNoticeService.getNoticeDetail(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/api/admin/cs/notices/{id}")
    public ResponseEntity<ApiResponse<Void>> updateNotice(
            @PathVariable Long id,
            @Valid @RequestBody AdminNoticeUpdateRequest request) {
        adminNoticeService.updateNotice(id, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/api/admin/cs/notices/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(@PathVariable Long id) {
        adminNoticeService.deleteNotice(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
