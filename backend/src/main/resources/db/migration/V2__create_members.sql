-- V2: 회원
CREATE TABLE members (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255),
    name        VARCHAR(50)  NOT NULL,
    phone       VARCHAR(20),
    grade       ENUM('BRONZE','SILVER','GOLD','VIP') NOT NULL DEFAULT 'BRONZE',
    point       INT          NOT NULL DEFAULT 0,
    status      ENUM('ACTIVE','INACTIVE','BANNED','WITHDRAWN') NOT NULL DEFAULT 'ACTIVE',
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE member_addresses (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id       BIGINT NOT NULL,
    alias           VARCHAR(20),
    recipient_name  VARCHAR(50) NOT NULL,
    phone           VARCHAR(20) NOT NULL,
    zipcode         VARCHAR(10) NOT NULL,
    address         VARCHAR(255) NOT NULL,
    address_detail  VARCHAR(255),
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE social_accounts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT NOT NULL,
    provider    ENUM('KAKAO','NAVER','GOOGLE') NOT NULL,
    social_id   VARCHAR(100) NOT NULL,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uq_social (provider, social_id),
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE wishlists (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uq_wishlist (member_id, product_id),
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
