import apiClient from './client';
import type { ApiResponse, PageResponse, PresignedUrlResponse } from './adminProduct';

export type BannerPosition = 'MAIN_TOP' | 'MAIN_MIDDLE' | 'POPUP';

export interface AdminBannerResponse {
  id: number;
  title: string;
  imageUrl: string;
  linkUrl: string | null;
  position: BannerPosition;
  sortOrder: number;
  startAt: string | null;
  endAt: string | null;
  active: boolean;
}

export interface BannerCreateRequest {
  title: string;
  imageUrl: string;
  linkUrl?: string;
  position: BannerPosition;
  sortOrder: number;
  startAt?: string | null;
  endAt?: string | null;
}

export interface BannerUpdateRequest {
  title: string;
  imageUrl: string;
  linkUrl?: string;
  position: BannerPosition;
  sortOrder: number;
  startAt?: string | null;
  endAt?: string | null;
}

export interface BannerListParams {
  position?: BannerPosition;
  isActive?: boolean;
  page?: number;
  size?: number;
}

export const adminBannerApi = {
  getList: (params: BannerListParams) =>
    apiClient.get<ApiResponse<PageResponse<AdminBannerResponse>>>('/api/admin/banners', { params }),

  getDetail: (id: number) =>
    apiClient.get<ApiResponse<AdminBannerResponse>>(`/api/admin/banners/${id}`),

  create: (data: BannerCreateRequest) =>
    apiClient.post<ApiResponse<number>>('/api/admin/banners', data),

  update: (id: number, data: BannerUpdateRequest) =>
    apiClient.put<ApiResponse<void>>(`/api/admin/banners/${id}`, data),

  delete: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/api/admin/banners/${id}`),

  updateSort: (bannerIds: number[]) =>
    apiClient.put<ApiResponse<void>>('/api/admin/banners/sort', { bannerIds }),

  getPresignedUrl: (fileName: string, contentType: string) =>
    apiClient.post<ApiResponse<PresignedUrlResponse>>('/api/admin/banners/presigned-url', { fileName, contentType }),
};
