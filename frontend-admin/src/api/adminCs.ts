import apiClient from './client';
import type { ApiResponse, PageResponse } from './adminProduct';

export type InquiryStatus = 'PENDING' | 'ANSWERED' | 'CLOSED';
export type InquiryType = 'PRODUCT' | 'ORDER' | 'DELIVERY' | 'CANCEL' | 'ETC';

export interface InquiryAnswerResponse {
  id: number;
  adminName: string;
  content: string;
  createdAt: string;
}

export interface AdminInquiryResponse {
  id: number;
  memberName: string;
  memberEmail: string;
  productId: number | null;
  orderId: number | null;
  type: InquiryType;
  title: string;
  content: string;
  status: InquiryStatus;
  secret: boolean;
  createdAt: string;
  updatedAt: string;
  answers: InquiryAnswerResponse[] | null;
}

export interface InquiryListParams {
  status?: InquiryStatus;
  type?: InquiryType;
  keyword?: string;
  page?: number;
  size?: number;
}

export interface InquiryAnswerRequest {
  content: string;
}

export const adminCsApi = {
  getInquiryList: (params: InquiryListParams) =>
    apiClient.get<ApiResponse<PageResponse<AdminInquiryResponse>>>('/api/admin/cs/inquiries', { params }),

  getInquiryDetail: (id: number) =>
    apiClient.get<ApiResponse<AdminInquiryResponse>>(`/api/admin/cs/inquiries/${id}`),

  answerInquiry: (id: number, data: InquiryAnswerRequest) =>
    apiClient.post<ApiResponse<void>>(`/api/admin/cs/inquiries/${id}/answer`, data),

  closeInquiry: (id: number) =>
    apiClient.patch<ApiResponse<void>>(`/api/admin/cs/inquiries/${id}/close`),
};
