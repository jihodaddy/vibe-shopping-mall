-- V5: 결제
CREATE TABLE payments (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id          BIGINT NOT NULL UNIQUE,
    idempotency_key   VARCHAR(100) NOT NULL UNIQUE,
    method            ENUM('KAKAO_PAY','NAVER_PAY','TOSS','CARD') NOT NULL,
    amount            INT NOT NULL,
    pg_transaction_id VARCHAR(200),
    pg_order_id       VARCHAR(200),
    status            ENUM('READY','DONE','CANCELLED','FAILED') NOT NULL DEFAULT 'READY',
    paid_at           DATETIME(6),
    cancelled_at      DATETIME(6),
    cancel_reason     VARCHAR(200),
    raw_response      JSON,
    FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE payment_webhooks (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id   BIGINT,
    pg_type      ENUM('TOSS','KAKAO','NAVER') NOT NULL,
    event_type   VARCHAR(50),
    payload      JSON NOT NULL,
    status       ENUM('RECEIVED','PROCESSED','FAILED') NOT NULL DEFAULT 'RECEIVED',
    processed_at DATETIME(6),
    created_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (payment_id) REFERENCES payments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
