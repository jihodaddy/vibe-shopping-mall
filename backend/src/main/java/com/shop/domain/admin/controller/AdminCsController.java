package com.shop.domain.admin.controller;

import com.shop.domain.admin.dto.*;
import com.shop.domain.admin.service.AdminCsService;
import com.shop.domain.cs.entity.InquiryStatus;
import com.shop.domain.cs.entity.InquiryType;
import com.shop.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminCsController {

    private final AdminCsService adminCsService;

    @GetMapping("/api/admin/cs/inquiries")
    public ResponseEntity<ApiResponse<Page<AdminInquiryResponse>>> getInquiryList(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) InquiryStatus status,
            @RequestParam(required = false) InquiryType type,
            @RequestParam(required = false) String keyword) {
        Page<AdminInquiryResponse> page = adminCsService.getInquiryList(status, type, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/api/admin/cs/inquiries/{id}")
    public ResponseEntity<ApiResponse<AdminInquiryResponse>> getInquiryDetail(
            @PathVariable Long id) {
        AdminInquiryResponse response = adminCsService.getInquiryDetail(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/api/admin/cs/inquiries/{id}/answer")
    public ResponseEntity<ApiResponse<Void>> answerInquiry(
            @PathVariable Long id,
            @Valid @RequestBody AdminInquiryAnswerRequest request,
            @AuthenticationPrincipal Long adminId) {
        adminCsService.answerInquiry(id, adminId, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/api/admin/cs/inquiries/{id}/close")
    public ResponseEntity<ApiResponse<Void>> closeInquiry(@PathVariable Long id) {
        adminCsService.closeInquiry(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
