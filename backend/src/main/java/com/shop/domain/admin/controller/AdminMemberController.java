package com.shop.domain.admin.controller;

import com.shop.domain.admin.dto.*;
import com.shop.domain.admin.service.AdminMemberService;
import com.shop.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/api/admin/members")
    public ResponseEntity<ApiResponse<Page<AdminMemberResponse>>> getMemberList(
            @PageableDefault(size = 20) Pageable pageable,
            @ModelAttribute AdminMemberSearchCondition condition) {
        Page<AdminMemberResponse> page = adminMemberService.getMemberList(condition, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/api/admin/members/{id}")
    public ResponseEntity<ApiResponse<AdminMemberResponse>> getMemberDetail(
            @PathVariable Long id) {
        AdminMemberResponse response = adminMemberService.getMemberDetail(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/api/admin/members/{id}/grade")
    public ResponseEntity<ApiResponse<Void>> changeGrade(
            @PathVariable Long id,
            @Valid @RequestBody AdminMemberGradeUpdateRequest request) {
        adminMemberService.changeGrade(id, request.getGrade());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/api/admin/members/{id}/points")
    public ResponseEntity<ApiResponse<Void>> addPoint(
            @PathVariable Long id,
            @Valid @RequestBody AdminMemberPointRequest request) {
        adminMemberService.addPoint(id, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/api/admin/members/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminMemberStatusUpdateRequest request) {
        adminMemberService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
