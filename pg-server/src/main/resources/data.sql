INSERT INTO merchant (
    merchant_uid,
    name,
    api_key,
    callback_url,
    status,
    created_at,
    updated_at
) VALUES (
             'MRC-001',
             'test-merchant',
             'test-api-key-001',
             'http://localhost:8081/api/webhook/payment',
             'ACTIVE',
             NOW(),
             NOW()
         );