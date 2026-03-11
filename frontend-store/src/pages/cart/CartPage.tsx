import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { cartApi } from '../../api/cart'
import { useAuthStore } from '../../store/authStore'

export default function CartPage() {
  const { isAuthenticated } = useAuthStore()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const { data, isLoading } = useQuery({
    queryKey: ['cart'],
    queryFn: () => cartApi.getCart(),
    enabled: isAuthenticated,
  })

  const removeMutation = useMutation({
    mutationFn: (cartItemId: number) => cartApi.removeItem(cartItemId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cart'] }),
  })

  const guestItems = isAuthenticated
    ? []
    : JSON.parse(localStorage.getItem('guestCart') || '[]')

  const cartItems = isAuthenticated
    ? (data?.data.data?.items ?? [])
    : guestItems

  const total = cartItems.reduce((sum: number, item: any) => sum + item.price * item.quantity, 0)

  const handleRemove = (cartItemId: number) => {
    if (isAuthenticated) {
      removeMutation.mutate(cartItemId)
    } else {
      const updated = guestItems.filter((i: any) => i.productId !== cartItemId)
      localStorage.setItem('guestCart', JSON.stringify(updated))
      window.location.reload()
    }
  }

  const handleOrder = () => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }
    navigate('/order')
  }

  return (
    <div style={{ backgroundColor: 'var(--cream)', minHeight: '100vh' }}>
      <div className="max-w-2xl mx-auto px-6 py-12">
        <h1 className="text-2xl mb-8" style={{ fontFamily: "'Noto Serif KR', serif", color: 'var(--mocha)' }}>
          장바구니
        </h1>

        {isLoading ? (
          <p style={{ color: 'var(--warm-mid)' }}>불러오는 중...</p>
        ) : cartItems.length === 0 ? (
          <div className="text-center py-20">
            <p style={{ color: 'var(--warm-mid)' }}>장바구니가 비어있습니다.</p>
            <button onClick={() => navigate('/')}
                    className="mt-4 px-6 py-2 text-sm"
                    style={{ border: '1px solid var(--ink)', color: 'var(--ink)' }}>
              쇼핑 계속하기
            </button>
          </div>
        ) : (
          <>
            <div className="flex flex-col gap-4 mb-8">
              {cartItems.map((item: any) => (
                <div key={item.cartItemId ?? item.productId}
                     className="flex items-center gap-4 p-4"
                     style={{ backgroundColor: 'var(--paper)', border: '1px solid var(--linen)' }}>
                  <div className="w-16 h-16 flex-shrink-0"
                       style={{ backgroundColor: 'var(--linen)' }}>
                    {item.thumbnailUrl && (
                      <img src={item.thumbnailUrl} alt={item.productName ?? item.name}
                           className="w-full h-full object-cover" />
                    )}
                  </div>
                  <div className="flex-1">
                    <p className="text-sm" style={{ color: 'var(--ink)' }}>
                      {item.productName ?? item.name}
                    </p>
                    <p className="text-xs mt-1" style={{ color: 'var(--warm-mid)' }}>
                      {item.price?.toLocaleString()}원 × {item.quantity}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-medium" style={{ color: 'var(--mocha)' }}>
                      {(item.price * item.quantity)?.toLocaleString()}원
                    </p>
                    <button onClick={() => handleRemove(item.cartItemId ?? item.productId)}
                            className="text-xs mt-1"
                            style={{ color: 'var(--warm-mid)' }}>
                      삭제
                    </button>
                  </div>
                </div>
              ))}
            </div>

            <div className="flex justify-between items-center py-4"
                 style={{ borderTop: '1px solid var(--linen)' }}>
              <span className="text-sm" style={{ color: 'var(--warm-mid)' }}>합계</span>
              <span className="text-xl font-medium" style={{ color: 'var(--mocha)' }}>
                {total.toLocaleString()}원
              </span>
            </div>

            <button onClick={handleOrder}
                    className="w-full py-3 text-sm font-medium mt-4"
                    style={{ backgroundColor: 'var(--ink)', color: 'white' }}>
              주문하기
            </button>
          </>
        )}
      </div>
    </div>
  )
}
