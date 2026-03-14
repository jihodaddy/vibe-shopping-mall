-- V11: CS 문의 답변 테이블 추가, 문의 상태에 CLOSED 추가, 공지사항 view_count 추가

-- inquiries 테이블 status에 CLOSED 추가
ALTER TABLE inquiries
    MODIFY COLUMN status ENUM('PENDING','ANSWERED','CLOSED') NOT NULL DEFAULT 'PENDING';

-- inquiries 테이블에 updated_at 추가
ALTER TABLE inquiries
    ADD COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- 문의 답변 테이블 생성 (다중 답변 지원)
CREATE TABLE inquiry_answers (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    inquiry_id  BIGINT NOT NULL,
    admin_id    BIGINT NOT NULL,
    content     TEXT NOT NULL,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (inquiry_id) REFERENCES inquiries(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES admin_users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- notices 테이블에 view_count 추가
ALTER TABLE notices
    ADD COLUMN view_count INT NOT NULL DEFAULT 0;
