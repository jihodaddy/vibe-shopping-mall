package com.shop.domain.stats.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "daily_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_date", nullable = false, unique = true)
    private LocalDate statDate;

    @Column(name = "order_count", nullable = false)
    private int orderCount;

    @Column(name = "sales_amount", nullable = false)
    private long salesAmount;

    @Column(name = "refund_count", nullable = false)
    private int refundCount;

    @Column(name = "refund_amount", nullable = false)
    private long refundAmount;

    @Column(name = "new_member_count", nullable = false)
    private int newMemberCount;

    @Builder
    public DailyStat(LocalDate statDate, int orderCount, long salesAmount,
                     int refundCount, long refundAmount, int newMemberCount) {
        this.statDate = statDate;
        this.orderCount = orderCount;
        this.salesAmount = salesAmount;
        this.refundCount = refundCount;
        this.refundAmount = refundAmount;
        this.newMemberCount = newMemberCount;
    }
}
