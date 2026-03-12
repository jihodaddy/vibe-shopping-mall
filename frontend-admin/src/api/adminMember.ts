import apiClient from './client';
import type { PageResponse, ApiResponse } from './adminProduct';

export type MemberGrade = 'BRONZE' | 'SILVER' | 'GOLD' | 'VIP';
export type MemberStatus = 'ACTIVE' | 'INACTIVE' | 'BANNED' | 'WITHDRAWN';

export interface AdminMemberResponse {
  id: number;
  email: string;
  name: string;
  phone?: string;
  grade: MemberGrade;
  point: number;
  status: MemberStatus;
  createdAt: string;
}

export interface MemberListParams {
  keyword?: string;
  grade?: MemberGrade;
  status?: MemberStatus;
  page?: number;
  size?: number;
}

export interface AddPointRequest {
  amount: number;
  reason: string;
}

export const adminMemberApi = {
  getList: (params: MemberListParams) =>
    apiClient.get<ApiResponse<PageResponse<AdminMemberResponse>>>('/api/admin/members', { params }),

  getDetail: (id: number) =>
    apiClient.get<ApiResponse<AdminMemberResponse>>(`/api/admin/members/${id}`),

  changeGrade: (id: number, grade: MemberGrade) =>
    apiClient.patch<ApiResponse<void>>(`/api/admin/members/${id}/grade`, { grade }),

  addPoint: (id: number, data: AddPointRequest) =>
    apiClient.post<ApiResponse<void>>(`/api/admin/members/${id}/points`, data),

  updateStatus: (id: number, status: MemberStatus) =>
    apiClient.patch<ApiResponse<void>>(`/api/admin/members/${id}/status`, { status }),
};
