import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { cartApi } from '../../api/cart'
import { ordersApi } from '../../api/orders'

export default function OrderPage() {
  const navigate = useNavigate()
  const [receiverName, setReceiverName] = useState('')
  const [receiverPhone, setReceiverPhone] = useState('')
  const [zipCode, setZipCode] = useState('')
  const [address, setAddress] = useState('')
  const [addressDetail, setAddressDetail] = useState('')
  const [memo, setMemo] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')

  const { data } = useQuery({
    queryKey: ['cart'],
    queryFn: () => cartApi.getCart(),
  })

  const cartItems = data?.data.data ?? []
  const total = cartItems.reduce((sum: number, item: any) => sum + item.price * item.qty, 0)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (cartItems.length === 0) {
      setError('장바구니가 비어있습니다.')
      return
    }
    setSubmitting(true)
    setError('')
    try {
      const res = await ordersApi.createOrder({
        receiverName,
        receiverPhone,
        zipcode: zipCode,
        address,
        addressDetail,
        deliveryMemo: memo,
        paymentMethod: 'TOSS',
      })
      const { orderNumber, finalPrice } = res.data.data
      navigate(`/order/complete?orderNumber=${orderNumber}&amount=${finalPrice}`)
    } catch {
      setError('주문 처리 중 오류가 발생했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div style={{ backgroundColor: 'var(--cream)', minHeight: '100vh' }}>
      <div className="max-w-2xl mx-auto px-6 py-12">
        <h1 className="text-2xl mb-8" style={{ fontFamily: "'Noto Serif KR', serif", color: 'var(--mocha)' }}>
          주문서
        </h1>

        <form onSubmit={handleSubmit} className="flex flex-col gap-6">
          {/* Order Items Summary */}
          <section style={{ backgroundColor: 'var(--paper)', border: '1px solid var(--linen)', padding: 20 }}>
            <h2 className="text-sm font-medium mb-4" style={{ color: 'var(--ink)' }}>주문 상품</h2>
            {cartItems.map((item: any) => (
              <div key={item.id} className="flex justify-between py-2"
                   style={{ borderBottom: '1px solid var(--linen)' }}>
                <span className="text-sm" style={{ color: 'var(--ink)' }}>
                  {item.productName} × {item.qty}
                </span>
                <span className="text-sm" style={{ color: 'var(--mocha)' }}>
                  {(item.price * item.qty).toLocaleString()}원
                </span>
              </div>
            ))}
            <div className="flex justify-between mt-3">
              <span className="text-sm font-medium" style={{ color: 'var(--ink)' }}>합계</span>
              <span className="text-lg font-medium" style={{ color: 'var(--mocha)' }}>
                {total.toLocaleString()}원
              </span>
            </div>
          </section>

          {/* Shipping Info */}
          <section style={{ backgroundColor: 'var(--paper)', border: '1px solid var(--linen)', padding: 20 }}>
            <h2 className="text-sm font-medium mb-4" style={{ color: 'var(--ink)' }}>배송지 정보</h2>
            <div className="flex flex-col gap-3">
              {[
                { label: '수령인', value: receiverName, setter: setReceiverName, placeholder: '받으실 분', type: 'text' },
                { label: '연락처', value: receiverPhone, setter: setReceiverPhone, placeholder: '010-0000-0000', type: 'tel' },
                { label: '우편번호', value: zipCode, setter: setZipCode, placeholder: '우편번호', type: 'text' },
                { label: '주소', value: address, setter: setAddress, placeholder: '기본 주소', type: 'text' },
                { label: '상세주소', value: addressDetail, setter: setAddressDetail, placeholder: '상세 주소', type: 'text' },
              ].map(({ label, value, setter, placeholder, type }) => (
                <div key={label} className="flex items-center gap-3">
                  <span className="text-xs w-16 flex-shrink-0" style={{ color: 'var(--warm-mid)' }}>{label}</span>
                  <input type={type} value={value} onChange={e => setter(e.target.value)}
                         placeholder={placeholder} required
                         className="flex-1 px-3 py-2 text-sm outline-none"
                         style={{ border: '1px solid var(--linen)', backgroundColor: 'var(--cream)', color: 'var(--ink)' }} />
                </div>
              ))}
              <div className="flex items-start gap-3">
                <span className="text-xs w-16 flex-shrink-0 pt-2" style={{ color: 'var(--warm-mid)' }}>배송 메모</span>
                <textarea value={memo} onChange={e => setMemo(e.target.value)}
                          placeholder="배송 메모 (선택)"
                          rows={2}
                          className="flex-1 px-3 py-2 text-sm outline-none resize-none"
                          style={{ border: '1px solid var(--linen)', backgroundColor: 'var(--cream)', color: 'var(--ink)' }} />
              </div>
            </div>
          </section>

          {error && <p className="text-sm" style={{ color: 'var(--terra)' }}>{error}</p>}

          <button type="submit" disabled={submitting}
                  className="w-full py-3 text-sm font-medium"
                  style={{ backgroundColor: 'var(--ink)', color: 'white' }}>
            {submitting ? '처리 중...' : `${total.toLocaleString()}원 결제하기`}
          </button>
        </form>
      </div>
    </div>
  )
}
