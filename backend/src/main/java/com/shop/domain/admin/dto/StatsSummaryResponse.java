package com.shop.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatsSummaryResponse {
    private final long totalSalesAmount;
    private final int totalOrderCount;
    private final long totalRefundAmount;
    private final int totalRefundCount;
    private final int totalNewMemberCount;
}
