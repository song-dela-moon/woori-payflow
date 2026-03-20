-- DROP DATABASE IF EXISTS pg_db;
CREATE DATABASE IF NOT EXISTS pg_db;
USE pg_db;

-- ==============================
-- 1. merchant
-- 가맹점 정보
-- ==============================
CREATE TABLE IF NOT EXISTS merchant (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          merchant_uid VARCHAR(50) NOT NULL UNIQUE,
                          name VARCHAR(100) NOT NULL,
                          api_key VARCHAR(100) NOT NULL UNIQUE,
                          callback_url VARCHAR(255),
                          status VARCHAR(20) NOT NULL,
                          created_at DATETIME NOT NULL,
                          updated_at DATETIME NOT NULL
);

-- ==============================
-- 2. payment
-- 결제 본체(현재 상태 관리)
-- ==============================
CREATE TABLE IF NOT EXISTS payment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         payment_uid VARCHAR(50) NOT NULL UNIQUE,
                         merchant_id BIGINT NOT NULL,
                         order_id VARCHAR(100) NOT NULL,
                         product_name VARCHAR(200) NOT NULL,
                         amount BIGINT NOT NULL,
                         payment_method VARCHAR(20) NOT NULL,
                         status VARCHAR(30) NOT NULL,
                         approval_code VARCHAR(50),
                         failure_code VARCHAR(50),
                         failure_message VARCHAR(255),
                         approved_at DATETIME,
                         cancelled_amount BIGINT NOT NULL DEFAULT 0,
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,

                         CONSTRAINT fk_payment_merchant
                             FOREIGN KEY (merchant_id) REFERENCES merchant(id),

                         CONSTRAINT uq_payment_merchant_order
                             UNIQUE (merchant_id, order_id)
);

-- ==============================
-- 3. payment_transaction
-- 승인/취소 이력
-- ==============================
CREATE TABLE IF NOT EXISTS payment_transaction (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     transaction_uid VARCHAR(50) NOT NULL UNIQUE,
                                     payment_id BIGINT NOT NULL,
                                     transaction_type VARCHAR(20) NOT NULL,
                                     amount BIGINT NOT NULL,
                                     result_type VARCHAR(20) NOT NULL,
                                     external_code VARCHAR(50),
                                     external_message VARCHAR(255),
                                     approval_code VARCHAR(50),
                                     requested_at DATETIME NOT NULL,
                                     responded_at DATETIME,
                                     created_at DATETIME NOT NULL,

                                     CONSTRAINT fk_payment_transaction_payment
                                         FOREIGN KEY (payment_id) REFERENCES payment(id)
);

-- ==============================
-- 4. webhook_history
-- 가맹점 웹훅 발송 이력
-- ==============================
CREATE TABLE IF NOT EXISTS webhook_history (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 payment_id BIGINT NOT NULL,
                                 merchant_id BIGINT NOT NULL,
                                 event_type VARCHAR(30) NOT NULL,
                                 callback_url VARCHAR(255) NOT NULL,
                                 request_body TEXT NOT NULL,
                                 response_status INT,
                                 success BOOLEAN NOT NULL,
                                 sent_at DATETIME NOT NULL,

                                 CONSTRAINT fk_webhook_payment
                                     FOREIGN KEY (payment_id) REFERENCES payment(id),

                                 CONSTRAINT fk_webhook_merchant
                                     FOREIGN KEY (merchant_id) REFERENCES merchant(id)
);