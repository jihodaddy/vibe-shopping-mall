import { Link, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'
import { authApi } from '../../api/auth'

export default function Header() {
  const { isAuthenticated, logout } = useAuthStore()
  const navigate = useNavigate()

  const handleLogout = async () => {
    try {
      await authApi.logout()
    } finally {
      logout()
      navigate('/')
    }
  }

  return (
    <header style={{ backgroundColor: 'var(--paper)', borderBottom: '1px solid var(--linen)' }}
            className="sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
        <Link to="/"
              style={{ fontFamily: "'Noto Serif KR', serif", color: 'var(--mocha)' }}
              className="text-2xl font-medium tracking-wide">
          <em style={{ color: 'var(--terra)', fontStyle: 'italic' }}>봄</em>날
        </Link>

        <nav className="hidden md:flex gap-8">
          {['신상품', '컬렉션', '아우터', '상의', '하의', '가방', '세일'].map(cat => (
            <Link key={cat} to={`/products?category=${cat}`}
                  style={{ color: 'var(--warm-mid)', fontSize: '0.85rem', letterSpacing: '0.05em' }}
                  className="hover:text-[var(--ink)] transition-colors">
              {cat}
            </Link>
          ))}
        </nav>

        <div className="flex items-center gap-3">
          {isAuthenticated ? (
            <>
              <Link to="/mypage" style={{ color: 'var(--warm-mid)' }} className="text-sm">마이페이지</Link>
              <button onClick={handleLogout} style={{ color: 'var(--warm-mid)' }} className="text-sm">로그아웃</button>
            </>
          ) : (
            <Link to="/login" style={{ color: 'var(--warm-mid)' }} className="text-sm">로그인</Link>
          )}
          <Link to="/cart"
                style={{ backgroundColor: 'var(--ink)', color: 'white' }}
                className="px-3 py-1.5 text-sm rounded">
            장바구니
          </Link>
        </div>
      </div>
    </header>
  )
}
