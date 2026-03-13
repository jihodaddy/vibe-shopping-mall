-- V7: 쿠폰
CREATE TABLE coupons (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    code                VARCHAR(50) NOT NULL UNIQUE,
    name                VARCHAR(100) NOT NULL,
    type                ENUM('RATE','FIXED') NOT NULL,
    value               INT NOT NULL,
    min_order_price     INT NOT NULL DEFAULT 0,
    max_discount_price  INT,
    target              ENUM('ALL','CATEGORY','PRODUCT') NOT NULL DEFAULT 'ALL',
    target_id           BIGINT,
    start_at            DATETIME(6) NOT NULL,
    end_at              DATETIME(6) NOT NULL,
    total_qty           INT,
    used_qty            INT NOT NULL DEFAULT 0,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE member_coupons (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT NOT NULL,
    coupon_id   BIGINT NOT NULL,
    status      ENUM('UNUSED','USED','EXPIRED') NOT NULL DEFAULT 'UNUSED',
    issued_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    used_at     DATETIME(6),
    order_id    BIGINT,
    INDEX idx_member_coupons (member_id, status),
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (coupon_id) REFERENCES coupons(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
