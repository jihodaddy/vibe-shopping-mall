import client from './client'

export interface ProductListItem {
  id: number
  name: string
  price: number
  discountRate: number
  discountPrice: number
  thumbnailUrl: string | null
  isBest: boolean
  isNew: boolean
  status: string
}

export interface ProductDetail extends ProductListItem {
  description: string
  stockQty: number
  categoryName: string
  images: { id: number; url: string; isMain: boolean; sortOrder: number }[]
  options: { id: number; optionName: string; optionValue: string; addPrice: number; stockQty: number }[]
}

export const productsApi = {
  getList: (params?: { categoryId?: number; keyword?: string; sort?: string; page?: number; size?: number }) =>
    client.get<{ data: { content: ProductListItem[]; totalElements: number } }>('/api/v1/products', { params }),

  getDetail: (id: number) =>
    client.get<{ data: ProductDetail }>(`/api/v1/products/${id}`),
}
