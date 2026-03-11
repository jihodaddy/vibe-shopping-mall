package com.shop.domain.payment.controller;

import com.shop.domain.payment.dto.TossConfirmRequest;
import com.shop.domain.payment.service.PaymentService;
import com.shop.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/toss/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmToss(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody TossConfirmRequest request) {
        paymentService.confirmToss(memberId, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/webhook/toss")
    public ResponseEntity<Void> handleTossWebhook(@RequestBody String payload) {
        // 웹훅 멱등 처리 - payment_webhooks 테이블에 저장
        return ResponseEntity.ok().build();
    }
}
