import apiClient from './client';
import type { PageResponse, ApiResponse } from './adminProduct';

export type OrderStatus =
  | 'PENDING'
  | 'PAID'
  | 'PREPARING'
  | 'SHIPPED'
  | 'DELIVERED'
  | 'CANCELLED'
  | 'REFUND_REQUESTED'
  | 'REFUNDED'
  | 'EXCHANGED';

export interface AdminOrderItemResponse {
  id: number;
  productId: number;
  productName: string;
  optionInfo?: string;
  qty: number;
  price: number;
  status: string;
}

export interface AdminOrderResponse {
  id: number;
  orderNumber: string;
  memberId: number;
  receiverName: string;
  receiverPhone: string;
  receiverAddress?: string;
  deliveryMemo?: string;
  status: OrderStatus;
  finalPrice: number;
  createdAt: string;
  items: AdminOrderItemResponse[];
}

export interface OrderListParams {
  keyword?: string;
  status?: string;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}

export interface ShippingRequest {
  courier: string;
  trackingNumber: string;
}

export const adminOrderApi = {
  getList: (params: OrderListParams) =>
    apiClient.get<ApiResponse<PageResponse<AdminOrderResponse>>>('/api/admin/orders', { params }),

  getDetail: (id: number) =>
    apiClient.get<ApiResponse<AdminOrderResponse>>(`/api/admin/orders/${id}`),

  updateStatus: (id: number, status: string) =>
    apiClient.patch<ApiResponse<void>>(`/api/admin/orders/${id}/status`, { status }),

  updateShipping: (id: number, data: ShippingRequest) =>
    apiClient.post<ApiResponse<void>>(`/api/admin/orders/${id}/shipping`, data),

  bulkUploadShipping: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.post<ApiResponse<void>>('/api/admin/orders/shipping/bulk-upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  processRefund: (id: number, orderItemIds: number[]) =>
    apiClient.post<ApiResponse<void>>(`/api/admin/orders/${id}/refund`, { orderItemIds }),
};
