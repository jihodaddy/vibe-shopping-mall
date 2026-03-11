package com.shop.domain.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(nullable = false)
    private int amount;

    @Column(length = 200)
    private String pgTransactionId;

    @Column(length = 200)
    private String pgOrderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.READY;

    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;

    @Column(length = 200)
    private String cancelReason;

    @Builder
    public Payment(Long orderId, String idempotencyKey, PaymentMethod method,
                   int amount, PaymentStatus status) {
        this.orderId = orderId;
        this.idempotencyKey = idempotencyKey;
        this.method = method;
        this.amount = amount;
        this.status = status;
    }

    public void confirmPayment(String pgTransactionId, String pgOrderId) {
        this.status = PaymentStatus.DONE;
        this.pgTransactionId = pgTransactionId;
        this.pgOrderId = pgOrderId;
        this.paidAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        this.status = PaymentStatus.CANCELLED;
        this.cancelReason = reason;
        this.cancelledAt = LocalDateTime.now();
    }
}
