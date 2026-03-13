import apiClient from './client';
import type { ApiResponse, PageResponse } from './adminProduct';

export type CouponType = 'RATE' | 'FIXED';
export type CouponTarget = 'ALL' | 'CATEGORY' | 'PRODUCT';

export interface AdminCouponResponse {
  id: number;
  code: string;
  name: string;
  type: CouponType;
  value: number;
  minOrderPrice: number;
  maxDiscountPrice: number | null;
  target: CouponTarget;
  targetId: number | null;
  startAt: string;
  endAt: string;
  totalQty: number | null;
  usedQty: number;
  active: boolean;
  issuedCount: number;
}

export interface CouponCreateRequest {
  code: string;
  name: string;
  type: CouponType;
  value: number;
  minOrderPrice: number;
  maxDiscountPrice?: number | null;
  target: CouponTarget;
  targetId?: number | null;
  startAt: string;
  endAt: string;
  totalQty?: number | null;
}

export interface CouponUpdateRequest {
  name: string;
  type: CouponType;
  value: number;
  minOrderPrice: number;
  maxDiscountPrice?: number | null;
  target: CouponTarget;
  targetId?: number | null;
  startAt: string;
  endAt: string;
  totalQty?: number | null;
}

export interface CouponIssueRequest {
  couponId: number;
  memberIds?: number[] | null;
}

export interface CouponListParams {
  keyword?: string;
  type?: CouponType;
  isActive?: boolean;
  page?: number;
  size?: number;
}

export const adminCouponApi = {
  getList: (params: CouponListParams) =>
    apiClient.get<ApiResponse<PageResponse<AdminCouponResponse>>>('/api/admin/coupons', { params }),

  getDetail: (id: number) =>
    apiClient.get<ApiResponse<AdminCouponResponse>>(`/api/admin/coupons/${id}`),

  create: (data: CouponCreateRequest) =>
    apiClient.post<ApiResponse<number>>('/api/admin/coupons', data),

  update: (id: number, data: CouponUpdateRequest) =>
    apiClient.put<ApiResponse<void>>(`/api/admin/coupons/${id}`, data),

  deactivate: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/api/admin/coupons/${id}`),

  issue: (data: CouponIssueRequest) =>
    apiClient.post<ApiResponse<number>>('/api/admin/coupons/issue', data),
};
