import client from './client'

export interface CartItem {
  id: number
  productId: number
  productName: string
  optionId: number | null
  optionInfo: string | null
  price: number
  qty: number
  thumbnailUrl: string | null
}

export const cartApi = {
  getCart: () => client.get<{ data: CartItem[] }>('/api/v1/cart'),
  addItem: (data: { productId: number; optionId?: number; qty: number; guestKey?: string }) =>
    client.post('/api/v1/cart', data),
  removeItem: (cartItemId: number) => client.delete(`/api/v1/cart/${cartItemId}`),
  mergeGuest: (guestKey: string) => client.post(`/api/v1/cart/merge?guestKey=${guestKey}`),
}
