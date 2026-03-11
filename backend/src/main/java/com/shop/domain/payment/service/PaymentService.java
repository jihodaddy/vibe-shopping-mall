package com.shop.domain.payment.service;

import com.shop.domain.order.entity.Order;
import com.shop.domain.order.entity.OrderStatus;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.payment.dto.TossConfirmRequest;
import com.shop.domain.payment.entity.Payment;
import com.shop.domain.payment.entity.PaymentStatus;
import com.shop.domain.payment.repository.PaymentRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final TossPaymentService tossPaymentService;

    public void confirmToss(Long memberId, TossConfirmRequest request) {
        // 1. 멱등성 확인
        Payment payment = paymentRepository.findByIdempotencyKey(request.getOrderId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.DONE) {
            throw new BusinessException(ErrorCode.DUPLICATE_PAYMENT);
        }

        // 2. 금액 검증
        if (payment.getAmount() != request.getAmount()) {
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 3. 토스 결제 승인 API 호출
        Map<String, Object> tossResponse;
        try {
            tossResponse = tossPaymentService.confirm(
                request.getPaymentKey(), request.getOrderId(), request.getAmount());
        } catch (Exception e) {
            log.error("Toss confirm failed", e);
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }

        // 4. Payment 상태 업데이트
        payment.confirmPayment(
            (String) tossResponse.get("paymentKey"),
            (String) tossResponse.get("orderId")
        );

        // 5. Order 상태 PAID
        Order order = orderRepository.findById(payment.getOrderId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.updateStatus(OrderStatus.PAID);
        order.getItems().forEach(item -> item.updateStatus(
            com.shop.domain.order.entity.OrderItemStatus.PAID));
    }
}
