package com.shop.domain.order.controller;

import com.shop.domain.order.dto.OrderCreateRequest;
import com.shop.domain.order.dto.OrderCreateResponse;
import com.shop.domain.order.service.OrderService;
import com.shop.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody OrderCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.createOrder(memberId, request)));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long orderId) {
        orderService.cancelOrder(memberId, orderId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
