import client from './client'

export interface OrderCreateRequest {
  receiverName: string
  receiverPhone: string
  zipcode: string
  address: string
  addressDetail?: string
  deliveryMemo?: string
  memberCouponId?: number
  usePoint?: number
  paymentMethod: 'TOSS' | 'KAKAO_PAY' | 'NAVER_PAY' | 'CARD'
}

export interface OrderCreateResponse {
  orderNumber: string
  idempotencyKey: string
  finalPrice: number
}

export const ordersApi = {
  createOrder: (data: OrderCreateRequest) =>
    client.post<{ data: OrderCreateResponse }>('/api/v1/orders', data),
  cancelOrder: (orderId: number) => client.delete(`/api/v1/orders/${orderId}`),
  confirmToss: (data: { paymentKey: string; orderId: string; amount: number }) =>
    client.post('/api/v1/payments/toss/confirm', data),
}
