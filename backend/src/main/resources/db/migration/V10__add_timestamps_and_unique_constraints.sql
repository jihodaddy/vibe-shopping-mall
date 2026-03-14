-- V10: 쿠폰/배너 테이블에 타임스탬프 컬럼 추가 및 member_coupons 유니크 제약조건 추가

-- coupons 테이블에 created_at, updated_at 추가
ALTER TABLE coupons
    ADD COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ADD COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- member_coupons 테이블에 created_at, updated_at 추가
ALTER TABLE member_coupons
    ADD COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ADD COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- banners 테이블에 created_at, updated_at 추가
ALTER TABLE banners
    ADD COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ADD COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- member_coupons 테이블에 유니크 제약조건 추가
ALTER TABLE member_coupons
    ADD UNIQUE KEY uk_member_coupon (member_id, coupon_id);
