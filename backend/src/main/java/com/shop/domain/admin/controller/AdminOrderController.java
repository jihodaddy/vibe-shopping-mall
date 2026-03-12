package com.shop.domain.admin.controller;

import com.shop.domain.admin.dto.*;
import com.shop.domain.admin.service.AdminOrderService;
import com.shop.domain.order.entity.OrderStatus;
import com.shop.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping("/api/admin/orders")
    public ResponseEntity<ApiResponse<Page<AdminOrderResponse>>> getOrderList(
            @PageableDefault(size = 20) Pageable pageable,
            @ModelAttribute AdminOrderSearchCondition condition) {
        Page<AdminOrderResponse> page = adminOrderService.getOrderList(condition, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/api/admin/orders/{id}")
    public ResponseEntity<ApiResponse<AdminOrderResponse>> getOrderDetail(
            @PathVariable Long id) {
        AdminOrderResponse response = adminOrderService.getOrderDetail(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/api/admin/orders/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminOrderStatusUpdateRequest request) {
        adminOrderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/api/admin/orders/{id}/shipping")
    public ResponseEntity<ApiResponse<Void>> updateShipping(
            @PathVariable Long id,
            @Valid @RequestBody AdminShippingUpdateRequest request) {
        adminOrderService.updateShipping(id, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/api/admin/orders/shipping/bulk-upload")
    public ResponseEntity<ApiResponse<Void>> bulkUpdateShipping(
            @RequestParam("file") MultipartFile file) {
        adminOrderService.bulkUpdateShipping(file);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/api/admin/orders/{id}/refund")
    public ResponseEntity<ApiResponse<Void>> processRefund(
            @PathVariable Long id,
            @Valid @RequestBody AdminRefundRequest request) {
        adminOrderService.processRefund(id, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
