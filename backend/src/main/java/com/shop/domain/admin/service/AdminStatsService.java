package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.stats.entity.DailyStat;
import com.shop.domain.stats.repository.DailyStatRepository;
import com.shop.domain.stats.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatsService {

    private final DailyStatRepository dailyStatRepository;
    private final SearchLogRepository searchLogRepository;

    public SalesStatsResponse getSalesStats(LocalDate from, LocalDate to, StatsPeriod period) {
        validateDateRange(from, to);
        List<DailyStat> dailyStats = dailyStatRepository.findByStatDateBetweenOrderByStatDateAsc(from, to);

        List<SalesStatsResponse.SalesStatsItem> items = switch (period) {
            case DAILY -> aggregateDaily(dailyStats);
            case WEEKLY -> aggregateWeekly(dailyStats);
            case MONTHLY -> aggregateMonthly(dailyStats);
        };

        return SalesStatsResponse.builder().items(items).build();
    }

    public StatsSummaryResponse getStatsSummary(LocalDate from, LocalDate to) {
        validateDateRange(from, to);
        List<DailyStat> dailyStats = dailyStatRepository.findByStatDateBetweenOrderByStatDateAsc(from, to);

        long totalSales = dailyStats.stream().mapToLong(DailyStat::getSalesAmount).sum();
        int totalOrders = dailyStats.stream().mapToInt(DailyStat::getOrderCount).sum();
        long totalRefundAmount = dailyStats.stream().mapToLong(DailyStat::getRefundAmount).sum();
        int totalRefundCount = dailyStats.stream().mapToInt(DailyStat::getRefundCount).sum();
        int totalNewMembers = dailyStats.stream().mapToInt(DailyStat::getNewMemberCount).sum();

        return StatsSummaryResponse.builder()
                .totalSalesAmount(totalSales)
                .totalOrderCount(totalOrders)
                .totalRefundAmount(totalRefundAmount)
                .totalRefundCount(totalRefundCount)
                .totalNewMemberCount(totalNewMembers)
                .build();
    }

    public SearchKeywordResponse getTopSearchKeywords(LocalDate from, LocalDate to, int limit) {
        validateDateRange(from, to);
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        List<Object[]> results = searchLogRepository.findTopKeywords(fromDateTime, toDateTime);

        List<SearchKeywordResponse.KeywordItem> items = new ArrayList<>();
        int rank = 1;
        for (Object[] row : results) {
            if (rank > limit) break;
            items.add(SearchKeywordResponse.KeywordItem.builder()
                    .rank(rank++)
                    .keyword((String) row[0])
                    .searchCount((Long) row[1])
                    .build());
        }

        return SearchKeywordResponse.builder().items(items).build();
    }

    List<SalesStatsResponse.SalesStatsItem> aggregateDaily(List<DailyStat> stats) {
        return stats.stream()
                .map(s -> SalesStatsResponse.SalesStatsItem.builder()
                        .label(s.getStatDate().toString())
                        .orderCount(s.getOrderCount())
                        .salesAmount(s.getSalesAmount())
                        .refundCount(s.getRefundCount())
                        .refundAmount(s.getRefundAmount())
                        .newMemberCount(s.getNewMemberCount())
                        .build())
                .collect(Collectors.toList());
    }

    List<SalesStatsResponse.SalesStatsItem> aggregateWeekly(List<DailyStat> stats) {
        WeekFields weekFields = WeekFields.of(Locale.KOREA);
        Map<String, List<DailyStat>> grouped = new LinkedHashMap<>();

        for (DailyStat stat : stats) {
            int year = stat.getStatDate().getYear();
            int week = stat.getStatDate().get(weekFields.weekOfWeekBasedYear());
            String key = year + "-W" + String.format("%02d", week);
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(stat);
        }

        return grouped.entrySet().stream()
                .map(entry -> aggregateGroup(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    List<SalesStatsResponse.SalesStatsItem> aggregateMonthly(List<DailyStat> stats) {
        Map<String, List<DailyStat>> grouped = new LinkedHashMap<>();

        for (DailyStat stat : stats) {
            String key = stat.getStatDate().getYear() + "-" +
                    String.format("%02d", stat.getStatDate().getMonthValue());
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(stat);
        }

        return grouped.entrySet().stream()
                .map(entry -> aggregateGroup(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
        }
    }

    private SalesStatsResponse.SalesStatsItem aggregateGroup(String label, List<DailyStat> stats) {
        return SalesStatsResponse.SalesStatsItem.builder()
                .label(label)
                .orderCount(stats.stream().mapToInt(DailyStat::getOrderCount).sum())
                .salesAmount(stats.stream().mapToLong(DailyStat::getSalesAmount).sum())
                .refundCount(stats.stream().mapToInt(DailyStat::getRefundCount).sum())
                .refundAmount(stats.stream().mapToLong(DailyStat::getRefundAmount).sum())
                .newMemberCount(stats.stream().mapToInt(DailyStat::getNewMemberCount).sum())
                .build();
    }
}
