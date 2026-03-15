package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.stats.entity.DailyStat;
import com.shop.domain.stats.repository.DailyStatRepository;
import com.shop.domain.stats.repository.SearchLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AdminStatsServiceTest {

    @Mock
    private DailyStatRepository dailyStatRepository;

    @Mock
    private SearchLogRepository searchLogRepository;

    @InjectMocks
    private AdminStatsService adminStatsService;

    @Test
    void getSalesStats_daily_returnsOneItemPerDay() {
        // given
        LocalDate from = LocalDate.of(2026, 3, 1);
        LocalDate to = LocalDate.of(2026, 3, 3);
        List<DailyStat> stats = List.of(
                createDailyStat(from, 10, 100000L, 1, 5000L, 3),
                createDailyStat(from.plusDays(1), 15, 200000L, 2, 10000L, 5),
                createDailyStat(from.plusDays(2), 8, 80000L, 0, 0L, 2)
        );
        given(dailyStatRepository.findByStatDateBetweenOrderByStatDateAsc(from, to)).willReturn(stats);

        // when
        SalesStatsResponse response = adminStatsService.getSalesStats(from, to, StatsPeriod.DAILY);

        // then
        assertThat(response.getItems()).hasSize(3);
        assertThat(response.getItems().get(0).getLabel()).isEqualTo("2026-03-01");
        assertThat(response.getItems().get(0).getSalesAmount()).isEqualTo(100000L);
        assertThat(response.getItems().get(1).getOrderCount()).isEqualTo(15);
    }

    @Test
    void getSalesStats_monthly_aggregatesByMonth() {
        // given
        LocalDate from = LocalDate.of(2026, 2, 28);
        LocalDate to = LocalDate.of(2026, 3, 2);
        List<DailyStat> stats = List.of(
                createDailyStat(LocalDate.of(2026, 2, 28), 10, 100000L, 1, 5000L, 3),
                createDailyStat(LocalDate.of(2026, 3, 1), 15, 200000L, 2, 10000L, 5),
                createDailyStat(LocalDate.of(2026, 3, 2), 8, 80000L, 0, 0L, 2)
        );
        given(dailyStatRepository.findByStatDateBetweenOrderByStatDateAsc(from, to)).willReturn(stats);

        // when
        SalesStatsResponse response = adminStatsService.getSalesStats(from, to, StatsPeriod.MONTHLY);

        // then
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems().get(0).getLabel()).isEqualTo("2026-02");
        assertThat(response.getItems().get(0).getSalesAmount()).isEqualTo(100000L);
        assertThat(response.getItems().get(1).getLabel()).isEqualTo("2026-03");
        assertThat(response.getItems().get(1).getSalesAmount()).isEqualTo(280000L);
        assertThat(response.getItems().get(1).getOrderCount()).isEqualTo(23);
    }

    @Test
    void getSalesStats_weekly_aggregatesByWeek() {
        // given
        LocalDate from = LocalDate.of(2026, 3, 1);
        LocalDate to = LocalDate.of(2026, 3, 14);
        List<DailyStat> stats = List.of(
                createDailyStat(LocalDate.of(2026, 3, 2), 10, 100000L, 0, 0L, 1),
                createDailyStat(LocalDate.of(2026, 3, 3), 12, 120000L, 0, 0L, 2),
                createDailyStat(LocalDate.of(2026, 3, 9), 20, 300000L, 1, 5000L, 3),
                createDailyStat(LocalDate.of(2026, 3, 10), 18, 250000L, 0, 0L, 4)
        );
        given(dailyStatRepository.findByStatDateBetweenOrderByStatDateAsc(from, to)).willReturn(stats);

        // when
        SalesStatsResponse response = adminStatsService.getSalesStats(from, to, StatsPeriod.WEEKLY);

        // then
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems().get(0).getLabel()).matches("\\d{4}-W\\d{2}");
        assertThat(response.getItems().get(0).getSalesAmount()).isEqualTo(220000L);
        assertThat(response.getItems().get(0).getOrderCount()).isEqualTo(22);
        assertThat(response.getItems().get(1).getSalesAmount()).isEqualTo(550000L);
        assertThat(response.getItems().get(1).getOrderCount()).isEqualTo(38);
        assertThat(response.getItems().get(1).getNewMemberCount()).isEqualTo(7);
    }

    @Test
    void getStatsSummary_sumsTotals() {
        // given
        LocalDate from = LocalDate.of(2026, 3, 1);
        LocalDate to = LocalDate.of(2026, 3, 3);
        List<DailyStat> stats = List.of(
                createDailyStat(from, 10, 100000L, 1, 5000L, 3),
                createDailyStat(from.plusDays(1), 15, 200000L, 2, 10000L, 5)
        );
        given(dailyStatRepository.findByStatDateBetweenOrderByStatDateAsc(from, to)).willReturn(stats);

        // when
        StatsSummaryResponse response = adminStatsService.getStatsSummary(from, to);

        // then
        assertThat(response.getTotalSalesAmount()).isEqualTo(300000L);
        assertThat(response.getTotalOrderCount()).isEqualTo(25);
        assertThat(response.getTotalRefundAmount()).isEqualTo(15000L);
        assertThat(response.getTotalRefundCount()).isEqualTo(3);
        assertThat(response.getTotalNewMemberCount()).isEqualTo(8);
    }

    @Test
    void getTopSearchKeywords_returnsLimitedResults() {
        // given
        LocalDate from = LocalDate.of(2026, 3, 1);
        LocalDate to = LocalDate.of(2026, 3, 7);
        List<Object[]> mockResults = List.of(
                new Object[]{"원피스", 150L},
                new Object[]{"티셔츠", 120L},
                new Object[]{"청바지", 80L}
        );
        given(searchLogRepository.findTopKeywords(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(mockResults);

        // when
        SearchKeywordResponse response = adminStatsService.getTopSearchKeywords(from, to, 2);

        // then
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems().get(0).getRank()).isEqualTo(1);
        assertThat(response.getItems().get(0).getKeyword()).isEqualTo("원피스");
        assertThat(response.getItems().get(0).getSearchCount()).isEqualTo(150L);
        assertThat(response.getItems().get(1).getRank()).isEqualTo(2);
        assertThat(response.getItems().get(1).getKeyword()).isEqualTo("티셔츠");
    }

    private DailyStat createDailyStat(LocalDate date, int orders, long sales,
                                       int refundCount, long refundAmount, int newMembers) {
        return DailyStat.builder()
                .statDate(date)
                .orderCount(orders)
                .salesAmount(sales)
                .refundCount(refundCount)
                .refundAmount(refundAmount)
                .newMemberCount(newMembers)
                .build();
    }
}
