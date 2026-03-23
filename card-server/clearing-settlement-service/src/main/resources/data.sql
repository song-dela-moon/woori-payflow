-- Clearing Settlement Service 초기 데이터

-- 가맹점 수취 계좌(정산금 입금 계좌) 맵핑 정보
INSERT INTO merchant_accounts (merchant_id, bank_code, account_number, account_holder)
VALUES ('M001', '001', '0000000001', '테스트 가맹점');
