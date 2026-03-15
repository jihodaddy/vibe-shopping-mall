package com.shop.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SalesStatsResponse {

    private final List<SalesStatsItem> items;

    @Getter
    @Builder
    public static class SalesStatsItem {
        private final String label;
        private final int orderCount;
        private final long salesAmount;
        private final int refundCount;
        private final long refundAmount;
        private final int newMemberCount;
    }
}
