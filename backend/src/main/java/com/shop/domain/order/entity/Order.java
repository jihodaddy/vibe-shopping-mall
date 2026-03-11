package com.shop.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, unique = true, length = 30)
    private String orderNumber;

    @Column(nullable = false, length = 50)
    private String receiverName;

    @Column(nullable = false, length = 20)
    private String receiverPhone;

    @Column(nullable = false, length = 10)
    private String zipcode;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 255)
    private String addressDetail;

    @Column(length = 200)
    private String deliveryMemo;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private int couponDiscount = 0;

    @Column(nullable = false)
    private int pointDiscount = 0;

    @Column(nullable = false)
    private int shippingFee = 0;

    @Column(nullable = false)
    private int finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @Builder
    public Order(Long memberId, String orderNumber, String receiverName, String receiverPhone,
                 String zipcode, String address, String addressDetail, String deliveryMemo,
                 int totalPrice, int couponDiscount, int pointDiscount, int shippingFee, int finalPrice) {
        this.memberId = memberId;
        this.orderNumber = orderNumber;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.deliveryMemo = deliveryMemo;
        this.totalPrice = totalPrice;
        this.couponDiscount = couponDiscount;
        this.pointDiscount = pointDiscount;
        this.shippingFee = shippingFee;
        this.finalPrice = finalPrice;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }
}
