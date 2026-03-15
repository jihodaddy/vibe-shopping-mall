import apiClient from './client';
import type { ApiResponse } from './adminProduct';

export type StatsPeriod = 'DAILY' | 'WEEKLY' | 'MONTHLY';

export interface SalesStatsItem {
  label: string;
  orderCount: number;
  salesAmount: number;
  refundCount: number;
  refundAmount: number;
  newMemberCount: number;
}

export interface SalesStatsResponse {
  items: SalesStatsItem[];
}

export interface StatsSummaryResponse {
  totalSalesAmount: number;
  totalOrderCount: number;
  totalRefundAmount: number;
  totalRefundCount: number;
  totalNewMemberCount: number;
}

export interface KeywordItem {
  rank: number;
  keyword: string;
  searchCount: number;
}

export interface SearchKeywordResponse {
  items: KeywordItem[];
}

export const adminStatsApi = {
  getSalesStats: (from: string, to: string, period: StatsPeriod) =>
    apiClient.get<ApiResponse<SalesStatsResponse>>('/api/admin/stats/sales', {
      params: { from, to, period },
    }),

  getSummary: (from: string, to: string) =>
    apiClient.get<ApiResponse<StatsSummaryResponse>>('/api/admin/stats/summary', {
      params: { from, to },
    }),

  getSearchKeywords: (from: string, to: string, limit = 20) =>
    apiClient.get<ApiResponse<SearchKeywordResponse>>('/api/admin/stats/search-keywords', {
      params: { from, to, limit },
    }),
};
