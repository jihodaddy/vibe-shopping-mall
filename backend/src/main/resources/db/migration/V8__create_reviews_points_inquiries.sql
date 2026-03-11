-- V8: 리뷰 / 포인트 / 문의
CREATE TABLE reviews (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id       BIGINT NOT NULL,
    product_id      BIGINT NOT NULL,
    order_item_id   BIGINT NOT NULL UNIQUE,
    rating          TINYINT NOT NULL,
    content         TEXT NOT NULL,
    is_photo_review BOOLEAN NOT NULL DEFAULT FALSE,
    status          ENUM('PENDING','APPROVED','HIDDEN') NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (order_item_id) REFERENCES order_items(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE review_images (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id   BIGINT NOT NULL,
    url         VARCHAR(500) NOT NULL,
    sort_order  INT NOT NULL DEFAULT 0,
    FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE point_histories (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id     BIGINT NOT NULL,
    type          ENUM('EARN','USE','EXPIRE','ADMIN_GRANT','ADMIN_DEDUCT') NOT NULL,
    amount        INT NOT NULL,
    balance_after INT NOT NULL,
    description   VARCHAR(200) NOT NULL,
    order_id      BIGINT,
    expired_at    DATE,
    created_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_point_member (member_id, created_at),
    FOREIGN KEY (member_id) REFERENCES members(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE inquiries (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT NOT NULL,
    product_id  BIGINT,
    order_id    BIGINT,
    type        ENUM('PRODUCT','ORDER','DELIVERY','CANCEL','ETC') NOT NULL,
    title       VARCHAR(200) NOT NULL,
    content     TEXT NOT NULL,
    status      ENUM('PENDING','ANSWERED') NOT NULL DEFAULT 'PENDING',
    answer      TEXT,
    answered_at DATETIME(6),
    is_secret   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (member_id) REFERENCES members(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
