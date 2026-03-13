-- V9: 콘텐츠 / 통계
CREATE TABLE banners (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    link_url    VARCHAR(500),
    position    ENUM('MAIN_TOP','MAIN_MIDDLE','POPUP') NOT NULL,
    sort_order  INT NOT NULL DEFAULT 0,
    start_at    DATETIME(6),
    end_at      DATETIME(6),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notices (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    content     LONGTEXT NOT NULL,
    is_pinned   BOOLEAN NOT NULL DEFAULT FALSE,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE search_logs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    keyword     VARCHAR(200) NOT NULL,
    member_id   BIGINT,
    result_count INT NOT NULL DEFAULT 0,
    searched_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_search_keyword (keyword),
    INDEX idx_search_at (searched_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE daily_stats (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date           DATE NOT NULL UNIQUE,
    order_count         INT NOT NULL DEFAULT 0,
    sales_amount        BIGINT NOT NULL DEFAULT 0,
    refund_count        INT NOT NULL DEFAULT 0,
    refund_amount       BIGINT NOT NULL DEFAULT 0,
    new_member_count    INT NOT NULL DEFAULT 0,
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
