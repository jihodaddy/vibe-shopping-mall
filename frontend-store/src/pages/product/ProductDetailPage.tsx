import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { productsApi } from '../../api/products'
import { cartApi } from '../../api/cart'
import { useAuthStore } from '../../store/authStore'

export default function ProductDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { isAuthenticated } = useAuthStore()
  const [quantity, setQuantity] = useState(1)
  const [adding, setAdding] = useState(false)
  const [message, setMessage] = useState('')

  const { data, isLoading } = useQuery({
    queryKey: ['product', id],
    queryFn: () => productsApi.getDetail(Number(id)),
    enabled: !!id,
  })

  const product = data?.data.data

  const handleAddToCart = async () => {
    if (!product) return
    setAdding(true)
    try {
      if (isAuthenticated) {
        await cartApi.addItem({ productId: product.id, qty: quantity })
      } else {
        const guest = JSON.parse(localStorage.getItem('guestCart') || '[]')
        const existing = guest.find((i: any) => i.productId === product.id)
        if (existing) existing.quantity += quantity
        else guest.push({ productId: product.id, name: product.name, price: product.price, quantity })
        localStorage.setItem('guestCart', JSON.stringify(guest))
      }
      setMessage('장바구니에 담겼습니다.')
      setTimeout(() => setMessage(''), 2000)
    } catch {
      setMessage('담기에 실패했습니다.')
    } finally {
      setAdding(false)
    }
  }

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center"
           style={{ backgroundColor: 'var(--cream)', color: 'var(--warm-mid)' }}>
        불러오는 중...
      </div>
    )
  }

  if (!product) {
    return (
      <div className="min-h-screen flex items-center justify-center"
           style={{ backgroundColor: 'var(--cream)', color: 'var(--warm-mid)' }}>
        상품을 찾을 수 없습니다.
      </div>
    )
  }

  return (
    <div style={{ backgroundColor: 'var(--cream)', minHeight: '100vh' }}>
      <div className="max-w-4xl mx-auto px-6 py-12">
        <div className="grid grid-cols-1 gap-12" style={{ gridTemplateColumns: '1fr 1fr' }}>
          {/* Image */}
          <div className="aspect-square" style={{ backgroundColor: 'var(--linen)' }}>
            {product.thumbnailUrl ? (
              <img src={product.thumbnailUrl} alt={product.name}
                   className="w-full h-full object-cover" />
            ) : (
              <div className="w-full h-full flex items-center justify-center"
                   style={{ color: 'var(--warm-mid)', fontSize: 14 }}>이미지 없음</div>
            )}
          </div>

          {/* Info */}
          <div className="flex flex-col">
            <p className="text-xs tracking-widest mb-2" style={{ color: 'var(--terra)' }}>
              {product.categoryName}
            </p>
            <h1 className="text-2xl mb-4" style={{ fontFamily: "'Noto Serif KR', serif", color: 'var(--mocha)' }}>
              {product.name}
            </h1>
            <p className="text-xl font-medium mb-6" style={{ color: 'var(--ink)' }}>
              {product.price?.toLocaleString()}원
            </p>

            <p className="text-sm leading-relaxed mb-8" style={{ color: 'var(--warm-mid)' }}>
              {product.description}
            </p>

            <div className="flex items-center gap-3 mb-4">
              <span className="text-sm" style={{ color: 'var(--warm-mid)' }}>수량</span>
              <div className="flex items-center" style={{ border: '1px solid var(--linen)' }}>
                <button className="w-8 h-8 text-lg flex items-center justify-center"
                        style={{ color: 'var(--ink)' }}
                        onClick={() => setQuantity(q => Math.max(1, q - 1))}>−</button>
                <span className="w-10 text-center text-sm" style={{ color: 'var(--ink)' }}>{quantity}</span>
                <button className="w-8 h-8 text-lg flex items-center justify-center"
                        style={{ color: 'var(--ink)' }}
                        onClick={() => setQuantity(q => q + 1)}>+</button>
              </div>
            </div>

            {product.stockQty === 0 ? (
              <button disabled className="w-full py-3 text-sm"
                      style={{ backgroundColor: 'var(--linen)', color: 'var(--warm-mid)' }}>
                품절
              </button>
            ) : (
              <button onClick={handleAddToCart} disabled={adding}
                      className="w-full py-3 text-sm font-medium mb-3"
                      style={{ backgroundColor: 'var(--ink)', color: 'white' }}>
                {adding ? '담는 중...' : '장바구니 담기'}
              </button>
            )}

            {message && (
              <p className="text-sm text-center mt-2" style={{ color: 'var(--terra)' }}>{message}</p>
            )}

            <button onClick={() => navigate('/cart')}
                    className="w-full py-3 text-sm mt-2"
                    style={{ border: '1px solid var(--ink)', color: 'var(--ink)', backgroundColor: 'transparent' }}>
              장바구니 보기
            </button>

            <p className="text-xs mt-6" style={{ color: 'var(--warm-mid)' }}>
              재고: {product.stockQty}개
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
