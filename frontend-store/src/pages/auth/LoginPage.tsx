import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../../api/auth'
import { useAuthStore } from '../../store/authStore'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const { setAccessToken } = useAuthStore()
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    try {
      const res = await authApi.login({ email, password })
      setAccessToken(res.data.data.accessToken)
      navigate('/')
    } catch {
      setError('이메일 또는 비밀번호가 올바르지 않습니다.')
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center"
         style={{ backgroundColor: 'var(--cream)' }}>
      <div className="w-full max-w-sm p-8" style={{ backgroundColor: 'var(--paper)', borderRadius: 2 }}>
        <h1 className="text-2xl mb-8 text-center"
            style={{ fontFamily: "'Noto Serif KR', serif", color: 'var(--mocha)' }}>
          <em style={{ color: 'var(--terra)', fontStyle: 'italic' }}>봄</em>날 로그인
        </h1>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            type="email" value={email} onChange={e => setEmail(e.target.value)}
            placeholder="이메일" required
            className="w-full px-4 py-3 text-sm outline-none"
            style={{ border: '1px solid var(--linen)', backgroundColor: 'var(--cream)', color: 'var(--ink)' }}
          />
          <input
            type="password" value={password} onChange={e => setPassword(e.target.value)}
            placeholder="비밀번호" required
            className="w-full px-4 py-3 text-sm outline-none"
            style={{ border: '1px solid var(--linen)', backgroundColor: 'var(--cream)', color: 'var(--ink)' }}
          />
          {error && <p className="text-sm" style={{ color: 'var(--terra)' }}>{error}</p>}
          <button type="submit"
                  className="w-full py-3 text-sm font-medium mt-2"
                  style={{ backgroundColor: 'var(--ink)', color: 'white' }}>
            로그인
          </button>
        </form>

        <p className="text-center text-sm mt-6" style={{ color: 'var(--warm-mid)' }}>
          계정이 없으신가요?{' '}
          <Link to="/signup" style={{ color: 'var(--terra)' }}>회원가입</Link>
        </p>
      </div>
    </div>
  )
}
