import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { productApi } from '../../api/products'

export default function HomePage() {
  const [page, setPage] = useState(0)
  const [keyword, setKeyword] = useState('')
  const [search, setSearch] = useState('')

  const { data, isLoading } = useQuery({
    queryKey: ['products', page, search],
    queryFn: () => productApi.getList({ page, size: 12, keyword: search || undefined }),
  })

  const products = data?.data.data?.content ?? []
  const totalPages = data?.data.data?.totalPages ?? 0

  return (
    <div style={{ backgroundColor: 'var(--cream)', minHeight: '100vh' }}>
      {/* Hero */}
      <section className="py-20 text-center"
               style={{ backgroundColor: 'var(--paper)', borderBottom: '1px solid var(--linen)' }}>
        <p className="text-xs tracking-widest mb-3" style={{ color: 'var(--terra)' }}>SPRING COLLECTION 2025</p>
        <h2 className="text-4xl mb-4" style={{ fontFamily: "'Noto Serif KR', serif", color: 'var(--mocha)' }}>
          봄날의 이야기
        </h2>
        <p className="text-sm" style={{ color: 'var(--warm-mid)' }}>
          자연에서 영감받은 따뜻한 일상의 물건들
        </p>
      </section>

      {/* Search */}
      <div className="max-w-4xl mx-auto px-6 py-8">
        <form onSubmit={e => { e.preventDefault(); setSearch(keyword); setPage(0) }}
              className="flex gap-2">
          <input
            value={keyword} onChange={e => setKeyword(e.target.value)}
            placeholder="상품 검색..."
            className="flex-1 px-4 py-2 text-sm outline-none"
            style={{ border: '1px solid var(--linen)', backgroundColor: 'var(--paper)', color: 'var(--ink)' }}
          />
          <button type="submit" className="px-6 py-2 text-sm"
                  style={{ backgroundColor: 'var(--ink)', color: 'white' }}>
            검색
          </button>
        </form>
      </div>

      {/* Products Grid */}
      <div className="max-w-4xl mx-auto px-6 pb-16">
        {isLoading ? (
          <div className="text-center py-20" style={{ color: 'var(--warm-mid)' }}>불러오는 중...</div>
        ) : products.length === 0 ? (
          <div className="text-center py-20" style={{ color: 'var(--warm-mid)' }}>상품이 없습니다.</div>
        ) : (
          <div className="grid grid-cols-2 gap-6" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))' }}>
            {products.map((p: any) => (
              <Link key={p.id} to={`/products/${p.id}`} className="group block">
                <div className="aspect-square mb-3 overflow-hidden"
                     style={{ backgroundColor: 'var(--linen)' }}>
                  {p.thumbnailUrl ? (
                    <img src={p.thumbnailUrl} alt={p.name}
                         className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center"
                         style={{ color: 'var(--warm-mid)', fontSize: 12 }}>이미지 없음</div>
                  )}
                </div>
                <p className="text-xs mb-1" style={{ color: 'var(--terra)' }}>{p.categoryName}</p>
                <p className="text-sm mb-1" style={{ color: 'var(--ink)' }}>{p.name}</p>
                <p className="text-sm font-medium" style={{ color: 'var(--mocha)' }}>
                  {p.price?.toLocaleString()}원
                </p>
                {p.stockQuantity === 0 && (
                  <p className="text-xs mt-1" style={{ color: 'var(--warm-mid)' }}>품절</p>
                )}
              </Link>
            ))}
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex justify-center gap-2 mt-12">
            {Array.from({ length: totalPages }, (_, i) => (
              <button key={i} onClick={() => setPage(i)}
                      className="w-8 h-8 text-sm"
                      style={{
                        backgroundColor: i === page ? 'var(--ink)' : 'transparent',
                        color: i === page ? 'white' : 'var(--warm-mid)',
                        border: '1px solid var(--linen)'
                      }}>
                {i + 1}
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
