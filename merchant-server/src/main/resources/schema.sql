CREATE TABLE merchant_order (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                order_uid VARCHAR(50) NOT NULL UNIQUE,
                                product_name VARCHAR(200) NOT NULL,
                                amount BIGINT NOT NULL,
                                order_status VARCHAR(30) NOT NULL,
                                created_at DATETIME NOT NULL,
                                updated_at DATETIME NOT NULL
);

CREATE TABLE merchant_payment (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  order_id BIGINT NOT NULL,
                                  pg_payment_id VARCHAR(50),
                                  amount BIGINT NOT NULL,
                                  payment_status VARCHAR(30) NOT NULL,
                                  approval_code VARCHAR(50),
                                  failure_code VARCHAR(50),
                                  failure_message VARCHAR(255),
                                  requested_at DATETIME NOT NULL,
                                  responded_at DATETIME,
                                  created_at DATETIME NOT NULL,
                                  CONSTRAINT fk_merchant_payment_order
                                      FOREIGN KEY (order_id) REFERENCES merchant_order(id)
);
