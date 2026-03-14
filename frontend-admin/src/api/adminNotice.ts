import apiClient from './client';
import type { ApiResponse, PageResponse } from './adminProduct';

export interface AdminNoticeResponse {
  id: number;
  title: string;
  content: string;
  pinned: boolean;
  active: boolean;
  viewCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface NoticeCreateRequest {
  title: string;
  content: string;
  isPinned: boolean;
}

export interface NoticeUpdateRequest {
  title: string;
  content: string;
  isPinned: boolean;
}

export interface NoticeListParams {
  keyword?: string;
  isActive?: boolean;
  page?: number;
  size?: number;
}

export const adminNoticeApi = {
  getList: (params: NoticeListParams) =>
    apiClient.get<ApiResponse<PageResponse<AdminNoticeResponse>>>('/api/admin/cs/notices', { params }),

  getDetail: (id: number) =>
    apiClient.get<ApiResponse<AdminNoticeResponse>>(`/api/admin/cs/notices/${id}`),

  create: (data: NoticeCreateRequest) =>
    apiClient.post<ApiResponse<number>>('/api/admin/cs/notices', data),

  update: (id: number, data: NoticeUpdateRequest) =>
    apiClient.put<ApiResponse<void>>(`/api/admin/cs/notices/${id}`, data),

  delete: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/api/admin/cs/notices/${id}`),
};
