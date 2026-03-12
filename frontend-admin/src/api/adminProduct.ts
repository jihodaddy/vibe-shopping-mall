import apiClient from './client';

export type ProductStatus = 'ON_SALE' | 'SOLD_OUT' | 'HIDDEN' | 'DELETED';

export interface ProductOption {
  id: number;
  name: string;
  value: string;
  additionalPrice: number;
  stockQty: number;
}

export interface AdminProductResponse {
  id: number;
  name: string;
  price: number;
  discountRate: number;
  stockQty: number;
  status: ProductStatus;
  description: string;
  categoryId: number;
  categoryName: string;
  imageUrls: Record<string, boolean>;
  options: ProductOption[];
  createdAt: string;
}

export interface AdminCategoryResponse {
  id: number;
  name: string;
  depth: number;
  sortOrder: number;
  active: boolean;
  children: AdminCategoryResponse[];
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface ApiResponse<T> {
  data: T;
}

export interface ProductListParams {
  keyword?: string;
  categoryId?: number;
  status?: string;
  page?: number;
  size?: number;
}

export interface ProductCreateRequest {
  name: string;
  price: number;
  discountRate: number;
  stockQty: number;
  description: string;
  categoryId: number;
  imageUrls?: Record<string, boolean>;
  status?: ProductStatus;
}

export interface ProductUpdateRequest extends ProductCreateRequest {
  status: ProductStatus;
}

export interface StockAdjustRequest {
  optionId?: number;
  delta: number;
}

export interface CategoryCreateRequest {
  parentId?: number;
  name: string;
  sortOrder: number;
}

export interface CategoryUpdateRequest {
  name: string;
  sortOrder: number;
  active: boolean;
}

export interface PresignedUrlResponse {
  presignedUrl: string;
  fileUrl: string;
}

export const adminProductApi = {
  getList: (params: ProductListParams) =>
    apiClient.get<ApiResponse<PageResponse<AdminProductResponse>>>('/api/admin/products', { params }),

  getDetail: (id: number) =>
    apiClient.get<ApiResponse<AdminProductResponse>>(`/api/admin/products/${id}`),

  create: (data: ProductCreateRequest) =>
    apiClient.post<ApiResponse<number>>('/api/admin/products', data),

  update: (id: number, data: ProductUpdateRequest) =>
    apiClient.put<ApiResponse<void>>(`/api/admin/products/${id}`, data),

  delete: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/api/admin/products/${id}`),

  bulkUpdateStatus: (ids: number[], status: ProductStatus) =>
    apiClient.patch<ApiResponse<void>>('/api/admin/products/status', { ids, status }),

  adjustStock: (id: number, data: StockAdjustRequest) =>
    apiClient.post<ApiResponse<void>>(`/api/admin/products/${id}/stock`, data),

  getPresignedUrl: (fileName: string, contentType: string) =>
    apiClient.post<ApiResponse<PresignedUrlResponse>>('/api/admin/files/presigned-url', { fileName, contentType }),

  getCategories: () =>
    apiClient.get<ApiResponse<AdminCategoryResponse[]>>('/api/admin/categories'),

  createCategory: (data: CategoryCreateRequest) =>
    apiClient.post<ApiResponse<number>>('/api/admin/categories', data),

  updateCategory: (id: number, data: CategoryUpdateRequest) =>
    apiClient.put<ApiResponse<void>>(`/api/admin/categories/${id}`, data),

  deleteCategory: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/api/admin/categories/${id}`),
};
