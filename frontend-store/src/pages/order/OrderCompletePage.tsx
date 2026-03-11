import { useSearchParams, useNavigate } from 'react-router-dom'

export default function OrderCompletePage() {
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const orderNumber = params.get('orderNumber')
  const amount = params.get('amount')

  return (
    <div className="min-h-screen flex items-center justify-center"
         style={{ backgroundColor: 'var(--cream)' }}>
      <div className="text-center p-12" style={{ backgroundColor: 'var(--paper)', borderRadius: 2, maxWidth: 400 }}>
        <div className="text-4xl mb-4">✓</div>
        <h1 className="text-xl mb-2" style={{ fontFamily: "'Noto Serif KR', serif", color: 'var(--mocha)' }}>
          주문이 완료되었습니다
        </h1>
        <p className="text-sm mb-1" style={{ color: 'var(--warm-mid)' }}>주문번호: {orderNumber}</p>
        <p className="text-sm mb-8" style={{ color: 'var(--warm-mid)' }}>
          결제금액: {Number(amount).toLocaleString()}원
        </p>
        <button onClick={() => navigate('/')}
                className="px-8 py-2 text-sm"
                style={{ backgroundColor: 'var(--ink)', color: 'white' }}>
          쇼핑 계속하기
        </button>
      </div>
    </div>
  )
}
