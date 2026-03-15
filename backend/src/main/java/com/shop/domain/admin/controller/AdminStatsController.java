package com.shop.domain.admin.controller;

import com.shop.domain.admin.dto.*;
import com.shop.domain.admin.service.AdminStatsService;
import com.shop.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @GetMapping("/api/admin/stats/sales")
    public ResponseEntity<ApiResponse<SalesStatsResponse>> getSalesStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "DAILY") StatsPeriod period) {
        SalesStatsResponse response = adminStatsService.getSalesStats(from, to, period);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/admin/stats/summary")
    public ResponseEntity<ApiResponse<StatsSummaryResponse>> getStatsSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        StatsSummaryResponse response = adminStatsService.getStatsSummary(from, to);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/admin/stats/search-keywords")
    public ResponseEntity<ApiResponse<SearchKeywordResponse>> getTopSearchKeywords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "20") int limit) {
        int cappedLimit = Math.min(limit, 100);
        SearchKeywordResponse response = adminStatsService.getTopSearchKeywords(from, to, cappedLimit);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
