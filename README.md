# Shopping Mall

Spring Boot + React 기반 풀스택 쇼핑몰 프로젝트.
고객몰(frontend-store)과 어드민 패널(frontend-admin)을 분리한 멀티 프론트엔드 구조.

---

## 기술 스택

| 영역 | 기술 |
|---|---|
| **Backend** | Spring Boot 3.2.3, Java 17, Spring Security, Spring Data JPA |
| **DB** | MySQL 8.0, Redis 7, Flyway (schema migration) |
| **인증** | JWT (Access Token + Redis 세션/블랙리스트) |
| **스토리지** | AWS S3 (presigned URL), LocalStack (로컬 개발) |
| **고객몰 Frontend** | React 18, TypeScript, Vite, TanStack Query, Zustand, React Router |
| **어드민 Frontend** | React 18, TypeScript, Vite, Ant Design 5, TanStack Query, Zustand, Recharts |
| **인프라** | Docker Compose (MySQL + Redis) |

---

## 프로젝트 구조

```
shopping-mall/
├── backend/                  # Spring Boot API 서버
│   └── src/main/java/com/shop/
│       ├── domain/
│       │   ├── admin/        # 어드민 API (인증, 상품, 주문, 회원 관리)
│       │   ├── member/       # 회원
│       │   ├── product/      # 상품, 카테고리
│       │   ├── order/        # 주문, 배송
│       │   ├── cart/         # 장바구니
│       │   ├── payment/      # 결제 (Toss Payments)
│       │   └── inventory/    # 재고 이력
│       └── global/           # 공통 (보안, 예외, S3, 이메일)
├── frontend-store/           # 고객몰 React 앱
├── frontend-admin/           # 어드민 패널 React 앱
├── infra/
│   └── docker/
│       └── docker-compose.yml
└── docs/                     # 설계 문서 및 구현 플랜
```

---

## 로컬 개발 환경 설정

### 1. 사전 요구사항

- Java 17
- Node.js 20+
- Docker Desktop

### 2. 인프라 실행 (MySQL + Redis)

```bash
cd infra/docker
docker compose up -d
```

| 서비스 | 포트 | 접속 정보 |
|---|---|---|
| MySQL | 3306 | root / root, DB: shopdb |
| Redis | 6379 | - |

### 3. 백엔드 실행

```bash
cd backend
./gradlew bootRun
```

기본 포트: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

> `application-local.yml`이 자동으로 활성화됩니다 (`spring.profiles.active=local`).

### 4. 고객몰 프론트엔드 실행

```bash
cd frontend-store
npm install
npm run dev
```

기본 포트: `http://localhost:5173`

### 5. 어드민 패널 실행

```bash
cd frontend-admin
npm install
npm run dev
```

기본 포트: `http://localhost:5174`

---

## API 엔드포인트

### 고객 API (`/api/v1/`)

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/api/v1/auth/signup` | 회원가입 |
| POST | `/api/v1/auth/login` | 로그인 |
| POST | `/api/v1/auth/logout` | 로그아웃 |
| GET | `/api/v1/products` | 상품 목록 |
| GET | `/api/v1/products/{id}` | 상품 상세 |
| GET | `/api/v1/categories` | 카테고리 목록 |
| GET/POST/DELETE | `/api/v1/cart` | 장바구니 |
| POST | `/api/v1/orders` | 주문 생성 |
| GET | `/api/v1/orders/{id}` | 주문 조회 |

### 어드민 API (`/api/admin/`)

| 메서드 | 경로 | 설명 | 권한 |
|---|---|---|---|
| POST | `/api/admin/auth/login` | 어드민 로그인 | 공개 |
| POST | `/api/admin/auth/logout` | 어드민 로그아웃 | ADMIN+ |
| GET/POST/PUT/DELETE | `/api/admin/products` | 상품 관리 | ADMIN+ |
| PATCH | `/api/admin/products/status` | 상품 일괄 상태 변경 | ADMIN+ |
| POST | `/api/admin/products/{id}/stock` | 재고 조정 | ADMIN+ |
| POST | `/api/admin/files/presigned-url` | S3 업로드 URL 발급 | ADMIN+ |
| GET/POST/PUT/DELETE | `/api/admin/categories` | 카테고리 관리 | ADMIN+ |
| GET | `/api/admin/orders` | 주문 목록 | ADMIN+ |
| PATCH | `/api/admin/orders/{id}/status` | 주문 상태 변경 | ADMIN+ |
| POST | `/api/admin/orders/{id}/shipping` | 배송 처리 (송장 입력) | ADMIN+ |
| POST | `/api/admin/orders/shipping/bulk-upload` | 엑셀 일괄 송장 업로드 | ADMIN+ |
| POST | `/api/admin/orders/{id}/refund` | 반품 처리 | ADMIN+ |
| GET | `/api/admin/members` | 회원 목록 | ADMIN+ |
| PATCH | `/api/admin/members/{id}/grade` | 회원 등급 변경 | ADMIN+ |
| POST | `/api/admin/members/{id}/points` | 포인트 지급 | ADMIN+ |
| PATCH | `/api/admin/members/{id}/status` | 회원 상태 변경 | ADMIN+ |
| DELETE | `/api/admin/**` | 삭제 작업 | SUPER_ADMIN |

### 권한 체계

| 역할 | 설명 |
|---|---|
| `SUPER_ADMIN` | 전체 권한 (삭제 포함) |
| `ADMIN` | 일반 관리 권한 |
| `CS_AGENT` | CS 전용 (향후 구현 예정) |

---

## 주요 기능

### Phase 1 — 고객몰
- 회원가입/로그인 (JWT 인증)
- 상품 목록/상세 조회
- 장바구니 담기/수정/삭제
- 주문 생성 및 결제 (Toss Payments 연동)
- 마이페이지 (주문 이력)

### Phase 2 — 어드민 패널
- **상품 관리**: 등록/수정/삭제, 이미지 S3 업로드, 카테고리 트리, 재고 조정
- **주문 관리**: 상태 변경, 배송 처리, 엑셀 일괄 송장 업로드, 반품 처리
- **회원 관리**: 목록/상세, 등급 변경, 포인트 지급, 계정 정지

---

## 환경 변수

### 백엔드 (`application-local.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopdb
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: local-dev-secret-key-must-be-at-least-32-chars
  access-expiration: 1800000    # 30분

cloud:
  aws:
    region: ap-northeast-2
    credentials:
      access-key: test
      secret-key: test
    endpoint: http://localhost:4566  # LocalStack
```

### 어드민 프론트엔드 (`frontend-admin/.env.development`)

```
VITE_API_BASE_URL=http://localhost:8080
```

---

## 테스트

```bash
# 전체 테스트
cd backend
./gradlew test

# 어드민 서비스 테스트만
./gradlew test --tests "com.shop.domain.admin.*"
```
