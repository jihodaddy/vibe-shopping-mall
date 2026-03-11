-- V4: 주문
CREATE TABLE orders (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id       BIGINT NOT NULL,
    order_number    VARCHAR(30) NOT NULL UNIQUE,
    receiver_name   VARCHAR(50) NOT NULL,
    receiver_phone  VARCHAR(20) NOT NULL,
    zipcode         VARCHAR(10) NOT NULL,
    address         VARCHAR(255) NOT NULL,
    address_detail  VARCHAR(255),
    delivery_memo   VARCHAR(200),
    total_price     INT NOT NULL,
    coupon_discount INT NOT NULL DEFAULT 0,
    point_discount  INT NOT NULL DEFAULT 0,
    shipping_fee    INT NOT NULL DEFAULT 0,
    final_price     INT NOT NULL,
    status          ENUM('PENDING','PAID','PREPARING','SHIPPED','DELIVERED',
                         'CANCELLED','REFUND_REQUESTED','REFUNDED','EXCHANGED')
                    NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (member_id) REFERENCES members(id),
    INDEX idx_orders_member_created (member_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_items (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT NOT NULL,
    product_id   BIGINT NOT NULL,
    option_id    BIGINT,
    product_name VARCHAR(200) NOT NULL,
    option_info  VARCHAR(200),
    qty          INT NOT NULL,
    price        INT NOT NULL,
    status       ENUM('PENDING','PAID','PREPARING','SHIPPED','DELIVERED',
                      'CANCELLED','RETURN_REQUESTED','RETURNED') NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_shipping (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT NOT NULL UNIQUE,
    courier         VARCHAR(50),
    tracking_number VARCHAR(50),
    shipped_at      DATETIME(6),
    delivered_at    DATETIME(6),
    FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE returns (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id       BIGINT NOT NULL,
    member_id      BIGINT NOT NULL,
    type           ENUM('RETURN','EXCHANGE') NOT NULL,
    reason         ENUM('CHANGE_OF_MIND','DEFECTIVE','WRONG_ITEM','ETC') NOT NULL,
    reason_detail  VARCHAR(500),
    status         ENUM('REQUESTED','APPROVED','REJECTED','COMPLETED') NOT NULL DEFAULT 'REQUESTED',
    refund_amount  INT,
    created_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (member_id) REFERENCES members(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE return_items (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    return_id     BIGINT NOT NULL,
    order_item_id BIGINT NOT NULL,
    qty           INT NOT NULL,
    FOREIGN KEY (return_id) REFERENCES returns(id),
    FOREIGN KEY (order_item_id) REFERENCES order_items(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
