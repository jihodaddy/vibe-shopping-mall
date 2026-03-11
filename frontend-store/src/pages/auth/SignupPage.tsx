import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../../api/auth'

export default function SignupPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [phone, setPhone] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    try {
      await authApi.signup({ email, password, name, phone })
      navigate('/login')
    } catch {
      setError('회원가입에 실패했습니다. 이미 사용중인 이메일일 수 있습니다.')
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center"
         style={{ backgroundColor: 'var(--cream)' }}>
      <div className="w-full max-w-sm p-8" style={{ backgroundColor: 'var(--paper)', borderRadius: 2 }}>
        <h1 className="text-2xl mb-8 text-center"
            style={{ fontFamily: "'Noto Serif KR', serif", color: 'var(--mocha)' }}>
          <em style={{ color: 'var(--terra)', fontStyle: 'italic' }}>봄</em>날 회원가입
        </h1>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            type="text" value={name} onChange={e => setName(e.target.value)}
            placeholder="이름" required
            className="w-full px-4 py-3 text-sm outline-none"
            style={{ border: '1px solid var(--linen)', backgroundColor: 'var(--cream)', color: 'var(--ink)' }}
          />
          <input
            type="email" value={email} onChange={e => setEmail(e.target.value)}
            placeholder="이메일" required
            className="w-full px-4 py-3 text-sm outline-none"
            style={{ border: '1px solid var(--linen)', backgroundColor: 'var(--cream)', color: 'var(--ink)' }}
          />
          <input
            type="password" value={password} onChange={e => setPassword(e.target.value)}
            placeholder="비밀번호 (8자 이상)" required minLength={8}
            className="w-full px-4 py-3 text-sm outline-none"
            style={{ border: '1px solid var(--linen)', backgroundColor: 'var(--cream)', color: 'var(--ink)' }}
          />
          <input
            type="tel" value={phone} onChange={e => setPhone(e.target.value)}
            placeholder="휴대폰 번호"
            className="w-full px-4 py-3 text-sm outline-none"
            style={{ border: '1px solid var(--linen)', backgroundColor: 'var(--cream)', color: 'var(--ink)' }}
          />
          {error && <p className="text-sm" style={{ color: 'var(--terra)' }}>{error}</p>}
          <button type="submit"
                  className="w-full py-3 text-sm font-medium mt-2"
                  style={{ backgroundColor: 'var(--ink)', color: 'white' }}>
            가입하기
          </button>
        </form>

        <p className="text-center text-sm mt-6" style={{ color: 'var(--warm-mid)' }}>
          이미 계정이 있으신가요?{' '}
          <Link to="/login" style={{ color: 'var(--terra)' }}>로그인</Link>
        </p>
      </div>
    </div>
  )
}
