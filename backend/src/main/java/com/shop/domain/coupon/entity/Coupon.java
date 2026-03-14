package com.shop.domain.coupon.entity;

import com.shop.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false)
    private int value;

    @Column(nullable = false)
    private int minOrderPrice;

    private Integer maxDiscountPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponTarget target = CouponTarget.ALL;

    private Long targetId;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    private Integer totalQty;

    @Column(nullable = false)
    private int usedQty = 0;

    @Column(nullable = false)
    private boolean isActive = true;

    @Builder
    public Coupon(String code, String name, CouponType type, int value,
                  int minOrderPrice, Integer maxDiscountPrice,
                  CouponTarget target, Long targetId,
                  LocalDateTime startAt, LocalDateTime endAt,
                  Integer totalQty) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.value = value;
        this.minOrderPrice = minOrderPrice;
        this.maxDiscountPrice = maxDiscountPrice;
        this.target = target != null ? target : CouponTarget.ALL;
        this.targetId = targetId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.totalQty = totalQty;
    }

    public void update(String name, CouponType type, int value,
                       int minOrderPrice, Integer maxDiscountPrice,
                       CouponTarget target, Long targetId,
                       LocalDateTime startAt, LocalDateTime endAt,
                       Integer totalQty) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.minOrderPrice = minOrderPrice;
        this.maxDiscountPrice = maxDiscountPrice;
        this.target = target;
        this.targetId = targetId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.totalQty = totalQty;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.endAt);
    }

    public boolean isExhausted() {
        return this.totalQty != null && this.usedQty >= this.totalQty;
    }

    public void incrementUsedQty() {
        this.usedQty++;
    }
}
